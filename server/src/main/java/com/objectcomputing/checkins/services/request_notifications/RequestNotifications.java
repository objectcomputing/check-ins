package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.List;

@Singleton
@PubSubListener
public class RequestNotifications {

    private final FeedbackRequestRepository feedbackReqRepo;
    private final CurrentUserServices currentUserServices;
    private final MemberProfile System;

    public RequestNotifications(FeedbackRequestRepository feedbackReqRepo, CurrentUserServices currentUserServices) {
        this.feedbackReqRepo = feedbackReqRepo;
        this.currentUserServices = currentUserServices;
        System = new MemberProfile();
    }

    @Subscription("Example Topic")
    public void onMessage(byte[] data) {
        LocalDate today = LocalDate.now();
        String requesteeID, subject;
        subject = "You have a Pending Feedback Request!";
        List<FeedbackRequest> todaysRequest = feedbackReqRepo.findByValues(null, null, null, today);
        FeedbackRequest current;
        int size = todaysRequest.size();
        for(int cnt = 0; cnt < size; cnt++) {
            current = todaysRequest.get(cnt);
            /**
             * This is where we will send out the emails automatically, using the subject, content, and recipients.
             * The subject will be the same for all of them,
             */
        }
    }

}
