package com.objectcomputing.checkins.onboard.signrequest;

import com.objectcomputing.checkins.services.onboard.signrequest.SignRequestCreateDTO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Map;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest(packages="onboard.signrequest")
public class SignRequestSendRequestControllerTest {

    @Inject
    @Client("/send-signrequest")
    HttpClient client;

    @Test
    public void testBadRequestExceptionThrownWithBlankEmail() {
        SignRequestCreateDTO signRequest = new SignRequestCreateDTO();
        signRequest.setEmail("");

        MutableHttpRequest<SignRequestCreateDTO> request = HttpRequest.POST("", signRequest).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }
}
