package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
public class SignRequestControllerTest {

    // Class to be tested
    private SignRequestController mockController;

    @BeforeEach
    public void init() {
        mockController = mock(SignRequestController.class);
    }

//    @Test
//    public void testGetDocumentsRequest() {
//        when(mockController.getData()).thenReturn("done");
//        assertEquals("done", mockController.getData());
//
//        verify(mockController, times(1)).getData();
//    }

    @Test
    public void emptyGetDocuments() {

    }

    @Test
    public void testSendRequest() {
        mockController.sendSignRequest();
        verify(mockController).sendSignRequest();
    }

    @Test
    public void testEmbedRequest() {
        mockController.embedSignRequest();
        verify(mockController).embedSignRequest();
    }

}
