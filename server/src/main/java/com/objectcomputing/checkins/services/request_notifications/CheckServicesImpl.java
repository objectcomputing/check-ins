package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetConfig;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.*;

@Singleton
public class CheckServicesImpl implements CheckServices {
    private final FeedbackRequestRepository feedbackReqRepository;
    public static final String FEEDBACK_REQUEST_NOTIFICATION_SUBJECT = "check-ins.application.feedback.notifications.subject";
    public static final String FEEDBACK_REQUEST_NOTIFICATION_CONTENT = "check-ins.application.feedback.notifications.content";
    public static final String submitURL = "https://checkins.objectcomputing.com/feedback/submit?requestId=";
    private EmailSender emailSender;
    private String notificationSubject;
    private String notificationContent;
    private final MemberProfileServices memberProfileServices;

    public CheckServicesImpl(FeedbackRequestRepository feedbackReqRepository,
                             @Named(MailJetConfig.HTML_FORMAT) EmailSender emailSender,
                             @Property(name = FEEDBACK_REQUEST_NOTIFICATION_SUBJECT) String notificationSubject,
                             @Property(name = FEEDBACK_REQUEST_NOTIFICATION_CONTENT) String notificationContent,
                             MemberProfileServices memberProfileServices) {
        this.feedbackReqRepository = feedbackReqRepository;
        this.emailSender = emailSender;
        this.notificationContent = notificationContent;
        this.notificationSubject = notificationSubject;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public boolean sendScheduledEmails() {
        LocalDate today = LocalDate.now();
        List<FeedbackRequest> feedbackRequests = feedbackReqRepository.findBySendDateBeforeAndStatusEqual(today, "scheduled");
        for (FeedbackRequest req: feedbackRequests) {
            MemberProfile from = memberProfileServices.getById(req.getCreatorId());
            String fromName  = from.getFirstName() + " " + from.getLastName();
            String newContent =  notificationContent + "<a href=\""+submitURL+req.getId()+"\">Check-Ins application</a>.";
            emailSender.sendEmail(fromName, from.getWorkEmail(), notificationSubject, newContent);
        }
        return true;
    }

}
