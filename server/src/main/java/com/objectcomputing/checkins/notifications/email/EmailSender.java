package com.objectcomputing.checkins.notifications.email;

public interface EmailSender {
    public void sendEmail(String subject, String content);
}