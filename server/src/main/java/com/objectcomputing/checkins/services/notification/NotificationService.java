package com.objectcomputing.checkins.services.notification;

import java.util.UUID;

public interface NotificationService {

    void sendNotification(UUID userId, String message);
}