package com.objectcomputing.checkins.notifications.all_types;

import com.objectcomputing.checkins.gcp.chat.GoogleChatBot;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBotEntryRepo;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class NotificationSender implements NotificationSenderInterface {
    private EmailSender emailSender;
    private GoogleChatBot googleChatBot;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationSender.class);

    public NotificationSender(EmailSender emailSender, GoogleChatBot googleChatBot) {
        this.emailSender = emailSender;
        this.googleChatBot = googleChatBot;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void setGoogleChatBot(GoogleChatBot googleChatBot) {
        this.googleChatBot = googleChatBot;
    }

    @Override
    public void sendNotification(String subject, String content, String... recipients) {
            emailSender.sendEmail(subject, content, recipients);
            googleChatBot.sendChat( content, recipients);


        }


    }

