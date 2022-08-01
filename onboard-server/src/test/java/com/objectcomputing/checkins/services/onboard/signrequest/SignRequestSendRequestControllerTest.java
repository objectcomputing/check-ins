package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MicronautTest
public class SignRequestSendRequestControllerTest {

    @Inject
    @Client("/send-signrequest")
    private HttpClient client;

    @Test
    public void testVerifyBasicInteractions() {
        SignRequestDTO request = new SignRequestDTO();
        request.setEmail("test");
        String a = request.getEmail();
        assertEquals("test", a);
        //verify(request, times(1)).setEmail("test");
    }

    @Test
    public void testBadRequestExceptionThrownWithBlankEmail() {
        SignRequestDTO signRequest = new SignRequestDTO();
        signRequest.setEmail("");

        MutableHttpRequest<SignRequestDTO> request = HttpRequest.POST("", signRequest).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
}