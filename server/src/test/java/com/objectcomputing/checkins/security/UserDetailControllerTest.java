package com.objectcomputing.checkins.security;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
public class UserDetailControllerTest {

    @Inject
    @Client("/user")
    private HttpClient client;

    @Inject
    Authentication mockAuthentication;

    @MockBean(Authentication.class)
    public Authentication getAuthentication() {
        return mock(Authentication.class);
    }

    @BeforeEach
    void setup() {
        reset(mockAuthentication);
    }

    //unauthenticated users
    @Test
    public void testUserDetailsReturnsEmptyMapForUnauthenticatedUsers() {

        when(mockAuthentication.equals(null)).thenReturn(true);

        HttpRequest request = HttpRequest.GET("");
        HttpResponse<Map> response = client.toBlocking().exchange(request, LinkedHashMap.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0, response.body().size());
    }

    //authenticated users
    @Test
    public void testUserDetailsReturnsMapForAuthenticatedUsers() {

        HttpRequest request = HttpRequest.GET("");
        HttpResponse<Map> response = client.toBlocking().exchange(request, LinkedHashMap.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(1, response.body().size());
    }
}
