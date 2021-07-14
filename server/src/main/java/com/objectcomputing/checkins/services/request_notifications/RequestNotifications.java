package com.objectcomputing.checkins.services.request_notifications;

import java.time.LocalDate;

@PubSubListener
public class RequestNotifications {

    @Subscription("Example")
    public void Check() {
        LocalDate today = LocalDate.now();

        System.out.println("Work would be done here...");
    }


}
