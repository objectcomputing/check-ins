package com.objectcomputing.checkins.services.email;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetConfig;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class EmailServicesImpl implements EmailServices {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServicesImpl.class);
    private EmailSender htmlEmailSender;
    private EmailSender textEmailSender;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileRepository memberProfileRepository;
    private final EmailRepository emailRepository;

    public EmailServicesImpl(@Named(MailJetConfig.HTML_FORMAT) EmailSender htmlEmailSender,
                             @Named(MailJetConfig.TEXT_FORMAT) EmailSender textEmailSender,
                             CurrentUserServices currentUserServices,
                             MemberProfileRepository memberProfileRepository,
                             EmailRepository emailRepository) {
        this.htmlEmailSender = htmlEmailSender;
        this.textEmailSender = textEmailSender;
        this.currentUserServices = currentUserServices;
        this.memberProfileRepository = memberProfileRepository;
        this.emailRepository = emailRepository;
    }

    public void setHtmlEmailSender(EmailSender emailSender) {
        this.htmlEmailSender = emailSender;
    }

    public void setTextEmailSender(EmailSender emailSender) {
        this.textEmailSender = emailSender;
    }

    @Override
    public List<Email> sendAndSaveEmail(String subject, String content, boolean html, String... recipients) {

        List<Email> sentEmails = new ArrayList<>();

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        LocalDateTime sendDate = LocalDateTime.now();
        boolean status;
        if (html) {
            status = htmlEmailSender.sendEmailReceivesStatus(subject, content, recipients);
        } else {
            status = textEmailSender.sendEmailReceivesStatus(subject, content, recipients);
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
                        LOG.warn(String.format("Prevented sending email to terminated member: %s", recipientEmail));
                    }
                }
            }
        } else {
            throw new RuntimeException("Failed to send emails");
        }

        return sentEmails;

    }
}
