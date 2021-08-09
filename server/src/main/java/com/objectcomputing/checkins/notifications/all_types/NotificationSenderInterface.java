package com.objectcomputing.checkins.notifications.all_types;

public interface NotificationSenderInterface {
    void sendNotification(String subject, String content, String... recipients);


}
