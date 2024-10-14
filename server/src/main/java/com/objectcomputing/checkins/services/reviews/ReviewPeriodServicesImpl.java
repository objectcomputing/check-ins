package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import io.micronaut.core.io.Readable;
import io.micronaut.core.io.IOUtils;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.BufferedReader;

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

    @Value("classpath:mjml/supervisor_review_assignment.mjml")
    private Readable supervisorReviewAssignmentTemplate;
    @Value("classpath:mjml/review_period_announcement.mjml")
    private Readable reviewPeriodAnnouncementTemplate;

    ReviewPeriodServicesImpl(ReviewPeriodRepository reviewPeriodRepository,
                                    ReviewAssignmentRepository reviewAssignmentRepository,
                                    MemberProfileRepository memberProfileRepository,
                                    FeedbackRequestServices feedbackRequestServices,
                                    ReviewStatusTransitionValidator reviewStatusTransitionValidator,
                                    @Named(MailJetFactory.MJML_FORMAT) EmailSender emailSender,
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

            validateDates(reviewPeriod);
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

        if (newStatus == ReviewStatus.OPEN) {
            // Ensure that the launch date is in the future.
            LocalDateTime launchDate = reviewPeriod.getLaunchDate();
            if (launchDate == null) {
                throw new BadArgException("Cannot open a review period without a launch date.");
            }
            if (launchDate.isBefore(LocalDateTime.now())) {
                throw new BadArgException("Cannot open a review period with a launch date in the past.");
            }
        }

        validateDates(reviewPeriod);

        if (newStatus == ReviewStatus.AWAITING_APPROVAL) {
            notifyRevieweeSupervisorsByReviewPeriod(reviewPeriod.getId(), reviewPeriod.getName());
        }

        // Update the review period and get the updated period.
        ReviewPeriod period = reviewPeriodRepository.update(reviewPeriod);

        if (period.getReviewStatus() == ReviewStatus.OPEN) {
            // If the review period has been updated and is now open, we need
            // to create feedback requests for all involved in the review period
            Set<ReviewAssignment> assignments =
                reviewAssignmentRepository.findByReviewPeriodId(period.getId());
            UUID reviewTemplateId = period.getReviewTemplateId();
            UUID selfReviewTemplateId = period.getSelfReviewTemplateId();
            LocalDate closeDate = period.getCloseDate().toLocalDate();
            LocalDate selfReviewCloseDate =
                period.getSelfReviewCloseDate().toLocalDate();

            // Log template id's that were not provided to the review period.
            // This is the reason a feedback request will not be created.
            if (reviewTemplateId == null) {
                LOG.warn("Review Period: " + period.getId().toString() +
                         " does not have a review template.");
            }
            if (selfReviewTemplateId == null) {
                LOG.warn("Review Period: " + period.getId().toString() +
                         " does not have a self-review template.");
            }

            Set<String> allInvolved = new HashSet<>();
            Set<UUID> selfRevieweeIds = new HashSet<>();
            for (ReviewAssignment assignment : assignments) {
                Optional<MemberProfile> reviewerProfile =
                  memberProfileRepository.findById(assignment.getReviewerId());
                if (!reviewerProfile.isEmpty()) {
                    allInvolved.add(reviewerProfile.get().getWorkEmail());
                }
                Optional<MemberProfile> revieweeProfile =
                  memberProfileRepository.findById(assignment.getRevieweeId());
                if (!revieweeProfile.isEmpty()) {
                    allInvolved.add(revieweeProfile.get().getWorkEmail());
                }

                // This person is being reviewed and will need a self-review
                // request.
                selfRevieweeIds.add(assignment.getRevieweeId());

                // Create the review feedback request.
                if (reviewTemplateId != null) {
                    createReviewRequest(
                        period, findCreatorId(assignment.getReviewerId()),
                        assignment.getRevieweeId(), assignment.getReviewerId(),
                        reviewTemplateId, closeDate);
                }
            }

            if (selfReviewTemplateId != null) {
                for(UUID memberId : selfRevieweeIds) {
                    // Create the self-review feedback request.
                    createReviewRequest(period, findCreatorId(memberId),
                                        memberId, memberId,
                                        selfReviewTemplateId,
                                        selfReviewCloseDate);
                }
            }

            String emailContent = constructReviewPeriodAnnouncementEmail(
                period.getName(), period.getPeriodStartDate(),
                period.getPeriodEndDate(), period.getLaunchDate(),
                period.getSelfReviewCloseDate(), period.getCloseDate()
            );
            emailSender.sendEmail(null, null, "It's time for performance reviews!", emailContent, allInvolved.toArray(new String[0]));
        }

        return period;
    }

    private String dateAsString(LocalDateTime dateTime) {
        String str = String.format("%s %d, %d",
                                   dateTime.getMonth(),
                                   dateTime.getDayOfMonth(),
                                   dateTime.getYear());
        return str.substring(0, 1) + str.substring(1).toLowerCase();
    }

    private String constructReviewPeriodAnnouncementEmail(
                       String reviewPeriodName, LocalDateTime startDate,
                       LocalDateTime endDate, LocalDateTime launchDate,
                       LocalDateTime selfReviewDate, LocalDateTime closeDate
) {
        try {
            return String.format(IOUtils.readText(
                            new BufferedReader(
                                reviewPeriodAnnouncementTemplate.asReader())),
                            reviewPeriodName, reviewPeriodName,
                            dateAsString(startDate), dateAsString(endDate),
                            dateAsString(launchDate),
                            dateAsString(selfReviewDate),
                            dateAsString(closeDate));
        } catch(Exception ex) {
            LOG.error(ex.toString());
            return "";
        }
    }

    private void notifyRevieweeSupervisorsByReviewPeriod(UUID reviewPeriodId, String reviewPeriodName) {
        if (environment.getActiveNames().contains(Environments.LOCAL)) return;
        Set<ReviewAssignment> reviewAssignments = reviewAssignmentRepository.findByReviewPeriodId(reviewPeriodId);
        Set<UUID> revieweeIds = reviewAssignments.stream().map(ReviewAssignment::getRevieweeId).collect(Collectors.toSet());
        Set<UUID> supervisorIds = new HashSet<>(memberProfileRepository.findSupervisoridByIdIn(revieweeIds));
        supervisorIds.removeIf(Objects::isNull);
        if (supervisorIds.isEmpty()) {
            LOG.info(String.format("Supervisors not found for Reviewees %s", revieweeIds));
            return;
        }

        Set<String> supervisorIdsToString = supervisorIds.stream().map(UUID::toString).collect(Collectors.toSet());
        List<String> supervisorEmails = memberProfileRepository.findWorkEmailByIdIn(supervisorIdsToString);

        // send notification to supervisors
        String emailContent = constructSupervisorEmail(reviewPeriodId, reviewPeriodName);
        emailSender.sendEmail(null, null, "Review Assignments Awaiting Approval", emailContent, supervisorEmails.toArray(new String[0]));
    }

    private String constructSupervisorEmail(UUID reviewPeriodId, String reviewPeriodName){
        try {
            return String.format(IOUtils.readText(
                            new BufferedReader(
                                supervisorReviewAssignmentTemplate.asReader())),
                            reviewPeriodName, webAddress, reviewPeriodId);
        } catch(Exception ex) {
            LOG.error(ex.toString());
            return "";
        }
    }

    private void validateDates(ReviewPeriod period) {
        // Check the self-review close date.
        LocalDateTime launchDate = period.getLaunchDate();
        LocalDateTime selfReviewCloseDate = period.getSelfReviewCloseDate();
        if (launchDate != null && selfReviewCloseDate != null &&
            !selfReviewCloseDate.isAfter(launchDate)) {
            throw new BadArgException("The review period self-review close date must be after the launch date.");
        }

        // Check the close date.
        LocalDateTime closeDate = period.getCloseDate();
        if (closeDate != null && selfReviewCloseDate != null &&
            !closeDate.isAfter(selfReviewCloseDate)) {
            throw new BadArgException("The review period close date must be after the self-review close date.");
        }

        // Check the period start date.
        LocalDateTime startDate = period.getPeriodStartDate();
        if (startDate != null && launchDate != null &&
            !startDate.isBefore(launchDate)) {
            throw new BadArgException("The review period start date must be before the launch date.");
        }

        // Check the period end date.
        LocalDateTime endDate = period.getPeriodEndDate();
        if (endDate != null && startDate != null &&
            !endDate.isAfter(startDate)) {
            throw new BadArgException("The review period end date must be after the start date.");
        }
        if (endDate != null && closeDate != null &&
            endDate.isAfter(closeDate)) {
            throw new BadArgException("The review period end date must be on or before the close date.");
        }
    }

    private UUID findCreatorId(UUID memberId) {
        List<MemberProfile> profile =
            memberProfileRepository.findSupervisorsForId(memberId);
        return profile.isEmpty() ? memberId : profile.get(0).getId();
    }

    private void createReviewRequest(ReviewPeriod period,
                                     UUID creatorId,
                                     UUID revieweeId,
                                     UUID reviewerId,
                                     UUID templateId,
                                     LocalDate dueDate) {
        try {
            LocalDate sendDate = LocalDate.now();
            FeedbackRequest request = new FeedbackRequest(
                creatorId, revieweeId, reviewerId, templateId, sendDate,
                dueDate, "sent", null, period.getId());
            feedbackRequestServices.save(request);
        } catch(Exception ex) {
            LOG.error(ex.toString());
        }
    }
}
