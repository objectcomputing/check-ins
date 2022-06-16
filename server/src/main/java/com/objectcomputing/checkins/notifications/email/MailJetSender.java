package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetServerException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Requires(property = MailJetSender.FROM_ADDRESS)
@Requires(property = MailJetSender.FROM_NAME)
@Singleton
public class MailJetSender implements EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(MailJetSender.class);
    private final MailjetClient client;
    private final EmailRepository emailRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileRepository memberProfileRepository;

    public static final String FROM_ADDRESS = "mail-jet.from_address";
    public static final String FROM_NAME = "mail-jet.from_name";
    public static final String TO_ADDRESS = "checkins@objectcomputing.com";
    public static final String TO_NAME = "Check-Ins Members";
    public static final int MAILJET_RECIPIENT_LIMIT = 50;

    private final String fromAddress;
    private final String fromName;

    public MailJetSender(MailjetClient client,
                         EmailRepository emailRepository,
                         CurrentUserServices currentUserServices,
                         MemberProfileRepository memberProfileRepository,
                         @Property(name = FROM_ADDRESS) String fromAddress,
                         @Property(name = FROM_NAME) String fromName) {
        this.client = client;
        this.emailRepository = emailRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileRepository = memberProfileRepository;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    public static List<JSONArray> getEmailBatches(String... recipients) {
        List<String> recipientList = new ArrayList<>(List.of(recipients));
        Collections.sort(recipientList);
        List<JSONArray> batches = new ArrayList<>();
        while (!recipientList.isEmpty()) {
            // Get only the first n elements limited by MailJet's API
            List<String> limitedRecipients = recipientList.stream()
                    .limit(MAILJET_RECIPIENT_LIMIT)
                    .collect(Collectors.toList());

            // Add each recipient to a JSON array to be sent in a MailJet request
            JSONArray recipientArray = new JSONArray();
            for (String recipient : limitedRecipients) {
                recipientArray.put(new JSONObject().put("Email", recipient));
            }

            // Update the list of batches and remove emails that have already been added
            batches.add(recipientArray);
            recipientList.removeAll(limitedRecipients);
        }

        return batches;
    }

    /**
     * This call sends a message to the given recipient with attachment.
     * @param subject, {@link String} Subject of email
     * @param content {@link String} Contents of email
     */
    @Override
    public void sendEmail(String subject, String content, String... recipients) {

        List<JSONArray> emailBatches = getEmailBatches(recipients);
        List<JSONArray> failedBatches = new ArrayList<>();
        JSONArray defaultRecipient = new JSONArray().put(new JSONObject()
                .put("Email", TO_ADDRESS)
                .put("Name", TO_NAME));

        emailBatches.forEach((recipientList) -> {
            try {
                MailjetRequest request = new MailjetRequest(Emailv31.resource)
                        .property(Emailv31.MESSAGES, new JSONArray()
                                .put(new JSONObject()
                                        .put(Emailv31.Message.FROM, new JSONObject()
                                                .put("Email", fromAddress)
                                                .put("Name", fromName))
                                        .put(Emailv31.Message.TO, defaultRecipient)
                                        .put(Emailv31.Message.BCC, recipientList)
                                        .put(Emailv31.Message.SUBJECT, subject)
                                        .put(Emailv31.Message.HTMLPART, content)));
                MailjetResponse response = client.post(request);
                LOG.info("Mailjet response status: " + response.getStatus());
                LOG.info("Mailjet response data: " + response.getData());
                System.out.println(response.getStatus());
                System.out.println(response.getData());
            } catch (MailjetException e) {
                LOG.error("An unexpected error occurred while sending the upload notification: " + e.getLocalizedMessage(), e);
                failedBatches.add(recipientList);
            } catch (MailjetSocketTimeoutException e) {
                LOG.error("An unexpected timeout occurred while sending the upload notification: " + e.getLocalizedMessage(), e);
                failedBatches.add(recipientList);
            }
        });

        if (!failedBatches.isEmpty()) {
            throw new RuntimeException("Failed to send emails for " + failedBatches);
        }
    }

    @Override
    public boolean sendEmailReceivesStatus(String subject, String content, String... recipients) {
        try {
            sendEmail(subject, content, recipients);
        } catch (Exception e){
            LOG.error("An unexpected exception occurred while sending the upload notification: "+ e.getLocalizedMessage(), e);
            return false;
        } catch (Error e) {
            LOG.error("An unexpected error occurred while sending the upload notification: "+ e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public List<Email> sendAndSaveEmail(String subject, String content, String... recipients) {

        List<Email> sentEmails = new ArrayList<>();

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        LocalDateTime sendDate = LocalDateTime.now();
        boolean status = sendEmailReceivesStatus(subject, content, recipients);

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
