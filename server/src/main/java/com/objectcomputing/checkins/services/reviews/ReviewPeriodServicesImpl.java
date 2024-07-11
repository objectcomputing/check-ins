package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.context.env.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
class ReviewPeriodServicesImpl implements ReviewPeriodServices {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewPeriodServicesImpl.class);

    private final ReviewPeriodRepository reviewPeriodRepository;
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final FeedbackRequestServices feedbackRequestServices;
    private final ReviewStatusTransitionValidator reviewStatusTransitionValidator;
    private EmailSender emailSender;
    private final Environment environment;
    private final String webAddress;

    ReviewPeriodServicesImpl(ReviewPeriodRepository reviewPeriodRepository,
                                    ReviewAssignmentRepository reviewAssignmentRepository,
                                    MemberProfileRepository memberProfileRepository,
                                    FeedbackRequestServices feedbackRequestServices,
                                    ReviewStatusTransitionValidator reviewStatusTransitionValidator,
                                    @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender,
                                    Environment environment,
                                    CheckInsConfiguration checkInsConfiguration) {
        this.reviewPeriodRepository = reviewPeriodRepository;
        this.reviewAssignmentRepository = reviewAssignmentRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.feedbackRequestServices = feedbackRequestServices;
        this.reviewStatusTransitionValidator = reviewStatusTransitionValidator;
        this.emailSender = emailSender;
        this.environment = environment;
        this.webAddress = checkInsConfiguration.getWebAddress();
    }

   void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public ReviewPeriod save(ReviewPeriod reviewPeriod) {
        ReviewPeriod newPeriod = null;
        if (reviewPeriod != null) {

            if (reviewPeriod.getId() != null) {
                throw new BadArgException(String.format("Found unexpected id %s for review period. New entities must not contain an id.",
                        reviewPeriod.getId()));
            } else if (reviewPeriodRepository.findByName(reviewPeriod.getName()).isPresent()) {
                throw new AlreadyExistsException(String.format("Review Period \"%s\" already exists.", reviewPeriod.getName()));
            }

            newPeriod = reviewPeriodRepository.save(reviewPeriod);
        }
        return newPeriod;
    }

    public ReviewPeriod findById(@NotNull UUID id) {
        return reviewPeriodRepository.findById(id).orElse(null);
    }

    public Set<ReviewPeriod> findByValue(String name, ReviewStatus reviewStatus) {
        Set<ReviewPeriod> reviewPeriods = new HashSet<>();

        if (name != null) {
            reviewPeriods = findByNameLike(name).stream()
                    .filter(rp -> reviewStatus == null || Objects.equals(rp.getReviewStatus(), reviewStatus))
                    .collect(Collectors.toSet());
        } else if (reviewStatus != null) {
            reviewPeriods.addAll(reviewPeriodRepository.findByReviewStatus(reviewStatus));
        } else {
            reviewPeriods.addAll(reviewPeriodRepository.findAll());
        }

        return reviewPeriods;
    }

    public void delete(@NotNull UUID id) {
        if (!feedbackRequestServices.findByValues(null, null, null, null, id, null, null).isEmpty()) {
            throw new BadArgException(String.format("Review Period %s has associated feedback requests and cannot be deleted", id));
        }
        reviewPeriodRepository.deleteById(id);
    }

    protected List<ReviewPeriod> findByNameLike(String name) {
        String wildcard = "%" + name + "%";
        return reviewPeriodRepository.findByNameIlike(wildcard);
    }

    public ReviewPeriod update(@NotNull ReviewPeriod reviewPeriod) {
        LOG.info("Updating entity {}", reviewPeriod);

        if (reviewPeriod.getId() == null) {
            throw new BadArgException("ReviewPeriod id is required for update");
        }

        Optional<ReviewPeriod> maybeExistingPeriod = reviewPeriodRepository.findById(reviewPeriod.getId());

        if (maybeExistingPeriod.isEmpty()) {
            throw new BadArgException(String.format("ReviewPeriod %s does not exist, cannot update", reviewPeriod.getId()));
        }

        ReviewPeriod existingPeriod = maybeExistingPeriod.get();
        ReviewStatus currentStatus = existingPeriod.getReviewStatus();
        ReviewStatus newStatus = reviewPeriod.getReviewStatus();
        if (!reviewStatusTransitionValidator.isValid(currentStatus, newStatus)) {
            throw new BadArgException(String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }

        if (newStatus == ReviewStatus.AWAITING_APPROVAL) {
            notifyRevieweeSupervisorsByReviewPeriod(reviewPeriod.getId(), reviewPeriod.getName());
        }

        return reviewPeriodRepository.update(reviewPeriod);
    }

    private void notifyRevieweeSupervisorsByReviewPeriod(UUID reviewPeriodId, String reviewPeriodName) {
        if (environment.getActiveNames().contains(Environments.LOCAL)) return;
        Set<ReviewAssignment> reviewAssignments = reviewAssignmentRepository.findByReviewPeriodId(reviewPeriodId);
        Set<UUID> revieweeIds = reviewAssignments.stream().map(ReviewAssignment::getRevieweeId).collect(Collectors.toSet());
        Set<UUID> supervisorIds = new HashSet<>(memberProfileRepository.findSupervisoridByIdIn(revieweeIds));
        if (supervisorIds.isEmpty()) {
            LOG.info(String.format("Supervisors not found for Reviewees %s", revieweeIds));
            return;
        }

        Set<String> supervisorIdsToString = supervisorIds.stream().map(UUID::toString).collect(Collectors.toSet());
        List<String> supervisorEmails = memberProfileRepository.findWorkEmailByIdIn(supervisorIdsToString);

        // send notification to supervisors
        String emailContent = constructEmailContent(reviewPeriodId, reviewPeriodName);
        emailSender.sendEmail(null, null, "Review Assignments Awaiting Approval", emailContent, supervisorEmails.toArray(new String[0]));
    }

    private String constructEmailContent (UUID reviewPeriodId, String reviewPeriodName){
        return """
                <h3>Review Assignments for Review Period '%s' are ready for your approval.</h3>\
                <a href="%s/feedback/reviews?period=%s">Click here</a> to review and approve reviewer assignments in the Check-Ins app.""".formatted(reviewPeriodName, webAddress, reviewPeriodId);
    }

}
