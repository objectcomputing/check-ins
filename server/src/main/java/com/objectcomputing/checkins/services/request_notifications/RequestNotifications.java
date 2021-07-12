package com.objectcomputing.checkins.services.request_notifications;

@PubSubListener
public class RequestNotifications {

    @Subscription("Example")
    public void Check() {
        System.out.println("Work would be done here...");
    }


}
