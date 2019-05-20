package com.objectcomputing;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import io.micronaut.context.annotation.Property;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.security.GeneralSecurityException;
import java.util.Properties;

@Singleton
public class GmailSender {

    private final HttpTransport httpTransport;
    private final JsonFactory jsonFactory;
    private final GoogleAuthenticator authenticator;
    private final String applicationName;

    @Inject
    GmailSender(
            @Property(name = "oci-google-drive.application.name") String applicationName,
            GoogleAuthenticator authenticator) throws GeneralSecurityException, IOException {
        this.applicationName = applicationName;
        this.authenticator = authenticator;
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.jsonFactory = JacksonFactory.getDefaultInstance();
    }

    Gmail getService() throws IOException {
        return new Gmail.Builder(httpTransport, jsonFactory, authenticator.setupCredentials())
                .setApplicationName(applicationName)
                .build();
    }

    void sendEmail(String subject, String content) {
        try {
            Gmail emailService = getService();

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage emailContent = new MimeMessage(session);
            emailContent.setFrom("kimberlinm@objectcomputing.com");
            emailContent.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress("kimberlinm@objectcomputing.com"));
            emailContent.setSubject(subject);
            emailContent.setText(content);

            Message message = createMessageWithEmail(emailContent);
            message = emailService.users().messages().send("kimberlinm@objectcomputing.com", message).execute();

            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Email Sent: " + subject);
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}