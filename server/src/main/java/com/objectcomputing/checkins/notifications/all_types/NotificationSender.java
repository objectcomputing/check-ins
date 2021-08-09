package com.objectcomputing.checkins.notifications.all_types;

import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetSender;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

@Singleton
public class NotificationSender implements NotificationSenderInterface {
    private final EmailSender emailSender;
    private final com.objectcomputing.checkings.gcp.chat.GoogleChatBot googleChatBot;
    private final MemberProfileServices memberProfileServices;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationSender.class);

    public NotificationSender(EmailSender emailSender, com.objectcomputing.checkings.gcp.chat.GoogleChatBot googleChatBot,
                              MemberProfileServices memberProfileServices) {
        this.emailSender = emailSender;
        this.googleChatBot = googleChatBot;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public void sendNotification(String subject, String content, String... recipients) {
        //parse recipient string to create list of emails
        JSONArray recipientList = new JSONArray();
        try {
            for (String recipient : recipients) {
                recipientList.put(new JSONObject().put("Email", recipient));
            }
        } catch(Error e ) {
            LOG.info("An unexpected error occurred while processing recipients: {} ", e.getMessage());
        } catch(Exception e ) {
            LOG.info("An unexpected exception occurred while processing recipients: {} ", e.getMessage());
        }

        //use parsed recipient string to get list of member IDs
        if (recipientList.length() > 0 ) {

        }

        //send email with list of parsed recipients
        //use google chat bot repo find by member id to see whcih reicpients have activated-->get space ID
        //use active space IDs and member IDs to send google chat bot message
    }
}
