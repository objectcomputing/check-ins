package com.objectcomputing.checkins.services.notification;

import io.micronaut.core.annotation.NonNull;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;


import java.util.UUID;

@Singleton
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final EmailSender emailSender;
    private final MemberProfileServices memberProfileServices;

    @Inject
    public NotificationServiceImpl(
            @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender,
            MemberProfileServices memberProfileServices
    ) {
        this.emailSender = emailSender;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public void sendNotification(@NonNull UUID userId, @NonNull String message) {
        MemberProfile user = memberProfileServices.getById(userId);
        
        if (user == null) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        String subject = "Feedback Request Denied";

        MemberProfile denier = getCurrentDenier();
        String content = "<h1>Feedback Request Denied</h1>" +
                "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                "<p>Your feedback request has been denied. The reason provided was:</p>" +
                "<p>" + message + "</p>" +
                "<p>Best Regards,</p>" +
                "<p>" + denier.getFirstName() + " " + denier.getLastName() + "</p>";

        
        String fromName = denier.getFirstName() + " " + denier.getLastName();
        String fromAddress = denier.getWorkEmail();

        emailSender.sendEmail(fromName, fromAddress, subject, content, user.getWorkEmail());
    }

    private MemberProfile getCurrentDenier() {
        // This method should fetch the profile of the user who denied the request
        // This could be passed as a parameter or retrieved from the session or context
        // If not available, you might need to redesign how this information is passed to the service
        throw new UnsupportedOperationException("Not implemented");
    }
}