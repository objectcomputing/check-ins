package com.objectcomputing.checkins.notifications.email;

import javax.inject.Inject;
import javax.inject.Singleton;

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

@Singleton
public class MailJetSender implements EmailSender {

    private static final Logger LOG = LoggerFactory.getLogger(MailJetSender.class);

    private MailjetClient client;

    @Inject
    public MailJetSender(MailjetClient client) {
        this.client = client;
    }

    /**
     * This call sends a message to the given recipient with attachment.
     * @param emailAddressToBodiesMap
     */
    // emailAddressToBodiesMap is email, address, email body
    public void sendEmail(String subject, String content) {
        MailjetRequest request;
        MailjetResponse response;
        try {
            request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", "kimberlinm@objectcomputing.com")
                                            .put("Name", "Michael Kimberlin"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", "hr@objectcomputing.com")))
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
