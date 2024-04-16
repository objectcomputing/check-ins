package com.objectcomputing.checkins.services.request_notifications;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServicesImpl;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CheckServicesImplTest {
    @Mock
    private FeedbackRequestServicesImpl feedbackRequestServices;
    @Mock
    private FeedbackRequestRepository feedbackRequestRepository;

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