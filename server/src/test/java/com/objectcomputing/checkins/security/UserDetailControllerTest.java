package com.objectcomputing.checkins.security;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.*;
import io.micronaut.test.annotation.MicronautTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest
public class UserDetailControllerTest {

    @Inject
    @Client("/user")
    private HttpClient client;

    @Inject
    UserDetailsController userDetailsController;

    private static Map<String, Object> userAttributes = new HashMap<>();

    @BeforeAll
    void setup() {
        userAttributes.put("email","bill@objectcomputing.com");
        userAttributes.put("picture","some.picture.url");
    }

    //unauthenticated users
    @Test
    public void testUserDetailsReturnsEmptyMapForUnauthenticatedUsers() {

        HttpRequest request = HttpRequest.GET("");
        HttpResponse<Map<String,Object>> response = client.toBlocking().exchange(request, Map.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0, Objects.requireNonNull(response.body()).size());
    }

    //authenticated users
    @Test
    public void testUserDetailsReturnsMapForAuthenticatedUsers() {

        Authentication auth = new Authentication() {
            @NotNull
            @Override
            public Map<String, Object> getAttributes() {
                return userAttributes;
            }

            @Override
            public String getName() {
                return null;
            }
        };

        HttpResponse<Map<String,Object>> response = userDetailsController.userDetails(auth);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, Objects.requireNonNull(response.body()).size());
        assertEquals(userAttributes.get("email"), Objects.requireNonNull(response.body()).get("email"));
        assertEquals(userAttributes.get("picture"), Objects.requireNonNull(response.body()).get("image_url"));
    }
}
