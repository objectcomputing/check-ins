package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import ch.digitalfondue.mjml4j.Mjml4j;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.settings.SettingOption;
import com.objectcomputing.checkins.services.settings.SettingsServices;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.context.annotation.Requires;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Prototype
public class MailJetSender implements EmailSender {
    public static final String MJMLPART = "MJMLPART";

    private static final Logger LOG = LoggerFactory.getLogger(MailJetSender.class);
    private final MailjetClient client;
    private final SettingsServices settingsServices;

    public static final int MAILJET_RECIPIENT_LIMIT = 49;

    private String emailFormat;

    private String getFromAddress() {
        try {
            return settingsServices.findByName(SettingOption.FROM_ADDRESS.name()).getValue();
        } catch (NotFoundException e) {
            return "";
        }
    }

    private String getFromName() {
        try {
            return settingsServices.findByName(SettingOption.FROM_NAME.name()).getValue();
        } catch (NotFoundException e) {
            return "";
        }
    }

    public MailJetSender(
            MailjetClient client,
            SettingsServices settingsServices
    ) {
        this.client = client;
        this.emailFormat = Emailv31.Message.HTMLPART;
        this.settingsServices = settingsServices;
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
                    .toList();

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
        if(fromName == null) fromName = getFromName();
        if(fromAddress == null) fromAddress = getFromAddress();

        String mj_apikey_public;
        String mj_apikey_private;
        try {
            mj_apikey_public = settingsServices.findByName(SettingOption.MJ_APIKEY_PUBLIC.name()).getValue();
        } catch (NotFoundException e) {
            mj_apikey_public = "";
        }
        try {
            mj_apikey_private = settingsServices.findByName(SettingOption.MJ_APIKEY_PRIVATE.name()).getValue();
        } catch (NotFoundException e) {
            mj_apikey_private = "";
        }

        if(mj_apikey_public == "" || mj_apikey_private == "") {
            LOG.error("API key(s) are missing for MailJetSender");
            return;
        }

        JSONObject sender = new JSONObject()
                .put("Email", fromAddress)
                .put("Name", fromName);

        String modifiedEmailFormat = emailFormat;
        if (modifiedEmailFormat.equals(MJMLPART)) {
            // Convert the MJML to HTML and update the local email format.
            var configuration = new Mjml4j.Configuration("en");
            content = Mjml4j.render(content, configuration);
            modifiedEmailFormat = Emailv31.Message.HTMLPART;
        }
        final String localEmailFormat = modifiedEmailFormat;
        final String localContent = content;

        if (recipients.length == 1) {
            final JSONArray to = new JSONArray()
                           .put(new JSONObject().put("Email", recipients[0]));
            try {
                send(sender, to, null, subject, localEmailFormat, localContent);
            } catch (MailjetException e) {
                throw new BadArgException("Failed to send email for " + to);
            }
        } else {
            List<JSONArray> emailBatches = getEmailBatches(recipients);
            List<JSONArray> failedBatches = new ArrayList<>();
            final JSONArray to =
                new JSONArray()
                .put(new JSONObject().put("Email", fromAddress));
            emailBatches.forEach((recipientList) -> {
                try {
                    send(sender, to, recipientList,
                         subject, localEmailFormat, localContent);
                } catch (MailjetException e) {
                    LOG.error("An unexpected error occurred while sending the upload notification: {}", e.getLocalizedMessage(), e);
                    failedBatches.add(recipientList);
                }
            });

            if (!failedBatches.isEmpty()) {
                throw new BadArgException("Failed to send emails for " + failedBatches);
            }
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
        if (format.equals(MJMLPART) ||
            format.equals(Emailv31.Message.HTMLPART) ||
            format.equals(Emailv31.Message.TEXTPART)) {
            this.emailFormat = format;
        } else {
            throw new BadArgException(String.format("Email format must be either HTMLPART, MJMLPART or TEXTPART, got %s", format));
        }
    }

    private void send(JSONObject from, JSONArray to, JSONArray bcc,
                      String subject, String emailFormat, String content) throws MailjetException {
        JSONObject values = new JSONObject()
                                .put(Emailv31.Message.FROM, from)
                                .put(Emailv31.Message.TO, to)
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(emailFormat, content);
        if (bcc != null) {
          values.put(Emailv31.Message.BCC, bcc);
        }

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray().put(values));
        MailjetResponse response = client.post(request);
        LOG.info("Mailjet response status: {}", response.getStatus());
        LOG.info("Mailjet response data: {}", response.getData());
    }
}
