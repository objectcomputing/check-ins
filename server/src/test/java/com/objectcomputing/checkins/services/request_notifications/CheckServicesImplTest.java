package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServicesImpl;
import com.objectcomputing.checkins.services.pulse.PulseServices;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Disabled in nativeTest, as we get an exception from Mockito
//     => java.lang.NoClassDefFoundError: Could not initialize class org.mockito.internal.configuration.plugins.Plugins
@DisabledInNativeImage
class CheckServicesImplTest extends TestContainersSuite {

    @Mock
    private FeedbackRequestServicesImpl feedbackRequestServices;

    @Mock
    private FeedbackRequestRepository feedbackRequestRepository;

    @Mock
    private PulseServices pulseServices;

    @Mock
    private ReviewPeriodServices reviewPeriodServices;

    @InjectMocks
    private CheckServicesImpl checkServices;

    AutoCloseable openMocks;

    @BeforeEach
    void initMocks() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void resetMocks() throws Exception {
        openMocks.close();
    }

    @Test
    void sendScheduledEmails() {
        FeedbackRequest retrievedRequest = new FeedbackRequest();
        retrievedRequest.setStatus("pending");
        List<FeedbackRequest> list = Collections.singletonList(retrievedRequest);
        when(feedbackRequestRepository.findBySendDateNotAfterAndStatusEqual(any(),eq("pending"))).thenReturn(list);
        checkServices.sendScheduledEmails();
        verify(feedbackRequestServices).sendNewRequestEmail(retrievedRequest);
        retrievedRequest.setStatus("sent");
        verify(feedbackRequestRepository).update(retrievedRequest);
    }
}
