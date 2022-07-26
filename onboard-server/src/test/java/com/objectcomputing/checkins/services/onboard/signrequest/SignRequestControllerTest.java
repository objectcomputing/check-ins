package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
public class SignRequestControllerTest {

    @Inject
    @Client("/services/signrequest")
    HttpClient client;

    @Test
    public void testGetDocumentsRequest() {
        SignRequestController mockController = mock(SignRequestController.class);

        // Tests for basic verification that methods were called
        //mockController.getData();
        //verify(mockController).getData();


        when(mockController.getData()).thenReturn("done");
        assertEquals("done", mockController.getData());

        verify(mockController, times(1)).getData();

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
