package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@PubSubListener
public class RequestNotifications {


    @Singleton
    @Subscription("Example Topic")
    public void Check() {
        LocalDate today = LocalDate.now();
        List<FeedbackRequest> TodaysRequest = feedbackReqServices.findByValues();
        System.out.println("Work would be done here...");
    }


}
