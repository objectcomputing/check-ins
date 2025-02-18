package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServicesImpl;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.services.pulse.PulseServices;
import com.objectcomputing.checkins.services.slack.kudos.KudosChannelReader;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Singleton
public class CheckServicesImpl implements CheckServices {

    private static final Logger LOG = LoggerFactory.getLogger(CheckServicesImpl.class);
    private final FeedbackRequestServicesImpl feedbackRequestServices;
    private final FeedbackRequestRepository feedbackRequestRepository;
    private final PulseServices pulseServices;
    private final ReviewPeriodServices reviewPeriodServices;
    private final KudosChannelReader kudosChannelReader;

    public CheckServicesImpl(FeedbackRequestServicesImpl feedbackRequestServices,
                             FeedbackRequestRepository feedbackRequestRepository,
                             PulseServices pulseServices,
                             ReviewPeriodServices reviewPeriodServices,
                             KudosChannelReader kudosChannelReader) {
        this.feedbackRequestServices = feedbackRequestServices;
        this.feedbackRequestRepository = feedbackRequestRepository;
        this.pulseServices = pulseServices;
        this.reviewPeriodServices = reviewPeriodServices;
        this.kudosChannelReader = kudosChannelReader;
    }

    @Override
    public boolean sendScheduledEmails() {
        LocalDate today = LocalDate.now();
        List<FeedbackRequest> feedbackRequests = feedbackRequestRepository.findBySendDateNotAfterAndStatusEqual(today, "pending");
        LOG.info("About to send {} emails", feedbackRequests.size());
        for (FeedbackRequest req: feedbackRequests) {
            feedbackRequestServices.sendNewRequestEmail(req);
            req.setStatus("sent");
            feedbackRequestRepository.update(req);
        }
        pulseServices.notifyUsers(today);
        reviewPeriodServices.sendNotifications(today);
        kudosChannelReader.readChannel();
        return true;
    }

}
