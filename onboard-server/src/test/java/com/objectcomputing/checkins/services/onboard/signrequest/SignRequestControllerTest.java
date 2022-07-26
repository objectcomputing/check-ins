package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

@MicronautTest
public class SignRequestControllerTest {

    @Inject
    @Client("/services/signrequest")
    HttpClient client;

    @Test
    public void testVerifyBasicInteractions() {
        SignRequestCreateDTO request = new SignRequestCreateDTO();
        request.setEmail("test");
        String a = request.getEmail();
        assertEquals("test", a);
    }

    @Test
    public void testGetDocumentsRequest() {
        SignRequestController mockController = mock(SignRequestController.class);

        // Tests for basic verification that methods were called
        mockController.getData();
        verify(mockController).getData();
    }

    @Test
    public void testSendRequest() {
        SignRequestController mockController = mock(SignRequestController.class);

        mockController.sendSignRequest();
        verify(mockController).sendSignRequest();
    }

    @Test
    public void testEmbedRequest() {
        SignRequestController mockController = mock(SignRequestController.class);

        mockController.embedSignRequest();
        verify(mockController).embedSignRequest();
    }

}
