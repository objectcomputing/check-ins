package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetServerException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;

@Singleton
public class MailJetSender implements EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(MailJetSender.class);
    private final MailjetClient client;

    public static final String FROM_ADDRESS = "mail-jet.from_address";
    public static final String FROM_NAME = "mail-jet.from_name";

    private final String fromAddress;
    private final String fromName;

    public MailJetSender(MailjetClient client,
                         @Property(name = FROM_ADDRESS) String fromAddress,
                         @Property(name = FROM_NAME) String fromName) {
        this.client = client;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    /**
     * This call sends a message to the given recipient with attachment.
     * @param subject, {@link String} Subject of email
     * @param content {@link String} Contents of email
     */
    // emailAddressToBodiesMap is email, address, email body
    public void sendEmail(String subject, String content, String... recipients) {
        MailjetRequest request;
        MailjetResponse response;
        try {
            JSONArray recipientList = new JSONArray();
            for (String recipient: recipients){
                recipientList.put(new JSONObject().put("Email", recipient));
            }
            request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", fromAddress)
                                            .put("Name", fromName))
                                    .put(Emailv31.Message.TO, recipientList)
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.HTMLPART, content)));
            response = client.post(request);
            LOG.info("Mailjet response status: " + response.getStatus());
            LOG.info("Mailjet response data: " + response.getData());
            System.out.println(response.getStatus());
            System.out.println(response.getData());
        } catch(MailjetServerException e) {
            LOG.error("An unexpected error occurred while sending the upload notification: "+ e.getLocalizedMessage(), e);
        } catch(MailjetException e) {
            LOG.error("An unexpected error occurred while sending the upload notification: "+ e.getLocalizedMessage(), e);
        } catch(MailjetSocketTimeoutException e) {
            LOG.error("An unexpected timeout occurred while sending the upload notification: "+ e.getLocalizedMessage(), e);
        }
    }
}
