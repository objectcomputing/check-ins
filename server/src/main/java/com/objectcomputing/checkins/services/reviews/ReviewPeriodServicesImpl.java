package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.email.AutomatedEmail;
import com.objectcomputing.checkins.services.email.AutomatedEmailRepository;
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
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.time.temporal.ChronoUnit;

@Singleton
class ReviewPeriodServicesImpl implements ReviewPeriodServices {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewPeriodServicesImpl.class);

    private final AutomatedEmailRepository automatedEmailRepository;
    private final ReviewPeriodRepository reviewPeriodRepository;
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final FeedbackRequestServices feedbackRequestServices;
    private final FeedbackRequestRepository feedbackRequestRepository;
    private final ReviewStatusTransitionValidator reviewStatusTransitionValidator;
    private EmailSender emailSender;
    private final Environment environment;
    private final String webAddress;

    private enum SelfReviewDate { LAUNCH, THREE_DAYS, ONE_DAY }

    @Value("classpath:mjml/supervisor_review_assignment.mjml")
    private Readable supervisorReviewAssignmentTemplate;
    @Value("classpath:mjml/review_period_announcement.mjml")
    private Readable reviewPeriodAnnouncementTemplate;
    @Value("classpath:mjml/self_review_reminder.mjml")
    private Readable selfReviewReminderTemplate;

    ReviewPeriodServicesImpl(ReviewPeriodRepository reviewPeriodRepository,
                                    ReviewAssignmentRepository reviewAssignmentRepository,
                                    MemberProfileRepository memberProfileRepository,
                                    FeedbackRequestServices feedbackRequestServices,
                                    FeedbackRequestRepository feedbackRequestRepository,
                                    ReviewStatusTransitionValidator reviewStatusTransitionValidator,
                                    @Named(MailJetFactory.MJML_FORMAT) EmailSender emailSender,
                                    Environment environment,
                                    CheckInsConfiguration checkInsConfiguration,
                                    AutomatedEmailRepository automatedEmailRepository) {
        this.reviewPeriodRepository = reviewPeriodRepository;
        this.reviewAssignmentRepository = reviewAssignmentRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.feedbackRequestServices = feedbackRequestServices;
        this.feedbackRequestRepository = feedbackRequestRepository;
        this.reviewStatusTransitionValidator = reviewStatusTransitionValidator;
        this.emailSender = emailSender;
        this.environment = environment;
        this.webAddress = checkInsConfiguration.getWebAddress();
        this.automatedEmailRepository = automatedEmailRepository;
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
        if (!feedbackRequestServices.findByValues(null, null, null, null, id, null, null, null).isEmpty()) {
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
            if (launchDate.isBefore(LocalDateTime.now().with(LocalTime.MIN))) {
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
            Set<ReviewAssignment> assignments = reviewAssignmentRepository.findByReviewPeriodId(period.getId());
            UUID reviewTemplateId = period.getReviewTemplateId();
            UUID selfReviewTemplateId = period.getSelfReviewTemplateId();
            LocalDate closeDate = period.getCloseDate().toLocalDate();
            LocalDate selfReviewCloseDate = period.getSelfReviewCloseDate().toLocalDate();

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
                        reviewTemplateId, closeDate, null);
                }
            }

            if (selfReviewTemplateId != null) {
                for(UUID memberId : selfRevieweeIds) {
                    // Create the self-review feedback request.
                    createReviewRequest(period, findCreatorId(memberId),
                                        memberId, memberId,
                                        selfReviewTemplateId,
                                        selfReviewCloseDate, null);
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

    private void createReviewRequest(
            ReviewPeriod period,
            UUID creatorId,
            UUID revieweeId,
            UUID reviewerId,
            UUID templateId,
            LocalDate dueDate, UUID externalRecipientId
    ) {
        try {
            LocalDate sendDate = LocalDate.now();
            FeedbackRequest request = new FeedbackRequest(
                creatorId, revieweeId, reviewerId, templateId, sendDate,
                dueDate, "sent", null, period.getId(), externalRecipientId);
            feedbackRequestServices.save(request);
        } catch(Exception ex) {
            LOG.error(ex.toString());
        }
    }

    public void sendNotifications(LocalDate today) {
        List<ReviewPeriod> openPeriods =
            reviewPeriodRepository.findByReviewStatus(ReviewStatus.OPEN);
        for(ReviewPeriod openPeriod : openPeriods) {
            for(SelfReviewDate date : SelfReviewDate.values()) {
                String key = "self_review_notification" +
                             openPeriod.getId().toString() + date.toString();
                Optional<AutomatedEmail> sent = automatedEmailRepository.findById(key);
                if (sent.isEmpty()) {
                    LocalDateTime check;
                    switch(date) {
                        case SelfReviewDate.LAUNCH:
                            check = openPeriod.getLaunchDate();
                            break;
                        case SelfReviewDate.THREE_DAYS:
                            check = openPeriod.getSelfReviewCloseDate();
                            if (check != null) {
                                check = check.minus(3, ChronoUnit.DAYS);
                            }
                            break;
                        default:
                        case SelfReviewDate.ONE_DAY:
                            check = openPeriod.getSelfReviewCloseDate();
                            if (check != null) {
                                check = check.minus(1, ChronoUnit.DAYS);
                            }
                            break;
                    }

                    if (check != null) {
                        if (today.isEqual(check.toLocalDate())) {
                            sendSelfReviewEmail(openPeriod.getId(), date);
                            automatedEmailRepository.save(new AutomatedEmail(key));
                        }
                    }
                }
            }
        }
    }

    void sendSelfReviewEmail(UUID reviewPeriodId, SelfReviewDate date) {
        Optional<ReviewPeriod> reviewPeriod =
            reviewPeriodRepository.findById(reviewPeriodId);
        if (reviewPeriod.isEmpty()) {
            LOG.error("Unable to find review period: " + reviewPeriodId.toString());
            return;
        }

        // Determine which subject we need to use.
        String subject = "";
        switch(date) {
            case SelfReviewDate.LAUNCH:
                subject = reviewPeriod.get().getName() + " has launched!";
                break;
            case SelfReviewDate.THREE_DAYS:
                subject = reviewPeriod.get().getName() +
                          " closes in three days!";
                break;
            default:
            case SelfReviewDate.ONE_DAY:
                subject = reviewPeriod.get().getName() + " closes in one day!";
                break;
        }

        try {
            // Read in the email template.
            String template = IOUtils.readText(
                                  new BufferedReader(
                                      selfReviewReminderTemplate.asReader()));

            // Get the set of self-reviewer email addresses.
            Set<MemberProfile> recipients = new HashSet<>();
            String templateId = null;
            List<FeedbackRequest> requests =
                feedbackRequestRepository.findByValues(null, null, null, null, reviewPeriodId.toString(), templateId, null);
            for (FeedbackRequest request : requests) {
                if (request.getRecipientId().equals(request.getRequesteeId())) {
                    Optional<MemberProfile> requesteeProfile =
                        memberProfileRepository.findById(
                            request.getRequesteeId());
                    if (!requesteeProfile.isEmpty()) {
                        recipients.add(requesteeProfile.get());
                    }
                }
            }

            List<String> addresses = recipients.stream()
                                         .map(p -> p.getWorkEmail()).toList();
            if (!addresses.isEmpty()) {
                // Customize the email content using the template.
                String content = String.format(
                                     template, webAddress,
                                     reviewPeriodId.toString(),
                                     dateAsString(reviewPeriod.get()
                                                    .getSelfReviewCloseDate()),
                                     webAddress);

                // Send out the email to everyone.
                emailSender.sendEmail(null, null, subject, content,
                                      addresses.toArray(
                                          new String[addresses.size()]));
            }
        } catch(Exception ex) {
            LOG.error("Send Self-Review Email: " + ex.toString());
        }
    }
}
