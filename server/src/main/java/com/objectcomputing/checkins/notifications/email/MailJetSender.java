package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.annotation.Requires;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Requires(property = MailJetSender.FROM_ADDRESS)
@Requires(property = MailJetSender.FROM_NAME)
@Prototype
public class MailJetSender implements EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(MailJetSender.class);
    private final MailjetClient client;

    public static final String FROM_ADDRESS = "mail-jet.from_address";
    public static final String FROM_NAME = "mail-jet.from_name";
    public static final int MAILJET_RECIPIENT_LIMIT = 49;

    private final String fromAddress;
    private final String fromName;
    private String emailFormat;

    public MailJetSender(MailjetClient client,
                         @Property(name = FROM_ADDRESS) String fromAddress,
                         @Property(name = FROM_NAME) String fromName) {
        this.client = client;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
        this.emailFormat = Emailv31.Message.HTMLPART;
    }

    /**
     * Helper method to divide a list of recipients into smaller lists (batches) of recipients
     * Enables sending emails to more recipients than permitted by a single MailJet API call
     * @param recipients List of recipient email addresses
     * @return list of {@link JSONArray}
     */
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
     * @param fromName {@link String} The name of the person sending the email
     * @param fromAddress {@link String} The email address of the person sending the email
     * @param subject {@link String} Subject of email
     * @param content {@link String} Contents of email
     * @param recipients List of recipient email addresses
     */
    @Override
    public void sendEmail(String fromName, String fromAddress, String subject, String content, String... recipients) {
        if(fromName == null) fromName = this.fromName;
        if(fromAddress == null) fromAddress = this.fromAddress;

        if(System.getenv("MJ_APIKEY_PUBLIC").isEmpty() || System.getenv("MJ_APIKEY_PRIVATE").isEmpty()) {
            LOG.error("API key(s) are missing for MailJetSender");
            return;
        }

        List<JSONArray> emailBatches = getEmailBatches(recipients);
        List<JSONArray> failedBatches = new ArrayList<>();
        JSONObject sender = new JSONObject()
                .put("Email", fromAddress)
                .put("Name", fromName);

        emailBatches.forEach((recipientList) -> {
            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, sender)
                                    .put(Emailv31.Message.TO, new JSONArray().put(sender))
                                    .put(Emailv31.Message.BCC, recipientList)
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(emailFormat, content)));
            try {
                MailjetResponse response = client.post(request);
                LOG.info("Mailjet response status: {}", response.getStatus());
                LOG.info("Mailjet response data: {}", response.getData());
            } catch (MailjetException e) {
                LOG.error("An unexpected error occurred while sending the upload notification: {}", e.getLocalizedMessage(), e);
                failedBatches.add(recipientList);
            }
        });

        if (!failedBatches.isEmpty()) {
            throw new BadArgException("Failed to send emails for " + failedBatches);
        }
    }

    @Override
    public boolean sendEmailReceivesStatus(String fromName, String fromAddress, String subject, String content, String... recipients) {
        try {
            sendEmail(fromName, fromAddress, subject, content, recipients);
        } catch (Exception e){
            LOG.error("An unexpected exception occurred while sending the upload notification: {}", e.getLocalizedMessage(), e);
            return false;
        } catch (Error e) {
            LOG.error("An unexpected error occurred while sending the upload notification: {}", e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void setEmailFormat(String format) {
        if (format.equals(Emailv31.Message.HTMLPART) || format.equals(Emailv31.Message.TEXTPART)) {
            this.emailFormat = format;
        } else {
            throw new BadArgException(String.format("Email format must be either HTMLPART or TEXTPART, got %s", format));
        }
    }
}
