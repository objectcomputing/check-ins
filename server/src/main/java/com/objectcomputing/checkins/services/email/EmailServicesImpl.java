package com.objectcomputing.checkins.services.email;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class EmailServicesImpl implements EmailServices {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServicesImpl.class);

    private final EmailSender htmlEmailSender;
    private final EmailSender textEmailSender;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileRepository memberProfileRepository;
    private final EmailRepository emailRepository;

    public EmailServicesImpl(@Named(MailJetFactory.HTML_FORMAT) EmailSender htmlEmailSender,
                             @Named(MailJetFactory.TEXT_FORMAT) EmailSender textEmailSender,
                             CurrentUserServices currentUserServices,
                             MemberProfileRepository memberProfileRepository,
                             EmailRepository emailRepository) {
        this.htmlEmailSender = htmlEmailSender;
        this.textEmailSender = textEmailSender;
        this.currentUserServices = currentUserServices;
        this.memberProfileRepository = memberProfileRepository;
        this.emailRepository = emailRepository;
    }

    @Override
    @RequiredPermission(Permission.CAN_SEND_EMAIL)
    public List<Email> sendAndSaveEmail(String subject, String content, boolean html, String... recipients) {

        List<Email> sentEmails = new ArrayList<>();

        MemberProfile currentUser = currentUserServices.getCurrentUser();
        String fromName = currentUser.getFirstName() + " " + currentUser.getLastName();
        LocalDateTime sendDate = LocalDateTime.now();
        boolean status;
        if (html) {
            status = htmlEmailSender.sendEmailReceivesStatus(fromName, currentUser.getWorkEmail(), subject, content, recipients);
        } else {
            status = textEmailSender.sendEmailReceivesStatus(fromName, currentUser.getWorkEmail(), subject, content, recipients);
        }

        UUID senderId = currentUserServices.getCurrentUser().getId();

        if (status) {
            for (String recipientEmail : recipients) {
                Optional<MemberProfile> recipient = memberProfileRepository.findByWorkEmail(recipientEmail);
                if (recipient.isPresent()) {
                    // Only send emails to unterminated members
                    LocalDate terminationDate = recipient.get().getTerminationDate();
                    if (terminationDate == null || terminationDate.isAfter(LocalDate.now())) {
                        UUID recipientId = recipient.get().getId();
                        Email email = new Email(subject, content, senderId, recipientId, sendDate, LocalDateTime.now());
                        Email savedEmail = emailRepository.save(email);
                        sentEmails.add(savedEmail);
                    } else {
                        LOG.warn("Prevented sending email to terminated member: {}", recipientEmail);
                    }
                }
            }
        } else {
            throw new RuntimeException("Failed to send emails");
        }

        return sentEmails;

    }
}
