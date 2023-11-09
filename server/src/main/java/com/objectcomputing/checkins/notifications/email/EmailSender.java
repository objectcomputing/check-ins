package com.objectcomputing.checkins.notifications.email;

public interface EmailSender {

    void sendEmail(String fromName, String fromAddress, String subject, String content, String... recipients);

    boolean sendEmailReceivesStatus(String fromName, String fromAddress, String subject, String content, String... recipients);

    void setEmailFormat(String format);
}