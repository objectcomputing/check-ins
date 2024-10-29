package com.objectcomputing.checkins.notifications.email;

public interface EmailSender {

    void sendEmailBlind(String fromName, String fromAddress, String subject, String content, String... recipients);

    void sendEmailExposed(String fromName, String fromAddress, String subject, String content, String recipient);

    boolean sendEmailReceivesStatus(String fromName, String fromAddress, String subject, String content, String... recipients);

    void setEmailFormat(String format);
}