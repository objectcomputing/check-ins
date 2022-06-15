package com.objectcomputing.checkins.notifications.email;

import java.util.List;

public interface EmailSender {
    void sendEmail(String subject, String content, boolean html, String... recipients);

    void sendEmail(String subject, String content, String... recipients);

    boolean sendEmailReceivesStatus(String subject, String content, boolean html, String... recipients);

    boolean sendEmailReceivesStatus(String subject, String content, String... recipients);

    List<Email> sendAndSaveEmail(String subject, String content, boolean html, String... recipients);
}