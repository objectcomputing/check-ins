package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.List;

@Singleton
public class CheckServicesImpl implements CheckServices {

    @Override
    public List<FeedbackRequest> GetTodaysRequests(LocalDate today) {
        List<FeedbackRequest> todaysRequests = null;

        return todaysRequests;
    }

}
