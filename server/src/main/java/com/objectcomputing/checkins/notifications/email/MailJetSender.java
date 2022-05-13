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
import java.util.Optional;
import java.util.UUID;

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

    /**
     * This call sends a message to the given recipient with attachment.
     * @param subject, {@link String} Subject of email
     * @param content {@link String} Contents of email
     */
    @Override
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

    @Override
    public boolean sendEmailReceivesStatus(String subject, String content, String... recipients) {
        try {
            sendEmail(subject, content, recipients);
        } catch(Exception e){
            LOG.error("An unexpected exception occurred while sending the upload notification: "+ e.getLocalizedMessage(), e);
            return false;
        } catch(Error e) {
            LOG.error("An unexpected error occurred while sending the upload notification: "+ e.getLocalizedMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public Email sendAndSaveEmail(String subject, String content, String... recipients) {

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        boolean status = sendEmailReceivesStatus(subject, content, recipients);

        UUID senderId = currentUserServices.getCurrentUser().getId();

        if (status) {
            for (String recipientEmail : recipients) {
                Optional<MemberProfile> recipient = memberProfileRepository.findByWorkEmail(recipientEmail);
                if (recipient.isPresent()) {
                    UUID recipientId = recipient.get().getId();
                    Email email = new Email(subject, content, senderId, recipientId, LocalDate.now());
                    emailRepository.save(email);
                }
            }
        }

        return null;
    }
}
