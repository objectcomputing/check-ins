package com.objectcomputing.checkins.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;

@MicronautTest  
public class BasicAuthTest {

    @Inject
    @Client("/home")
    RxHttpClient client; 

    @Test
    void unauthorizedUsersCannotUseService() {
        //when: 'Accessing a secured URL without authenticating'
        Executable e = () -> client.toBlocking().exchange(HttpRequest.GET("/").accept(MediaType.TEXT_PLAIN)); 

        // then: 'returns unauthorized'
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e); 
        assertEquals(thrown.getStatus(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void verifyHttpBasicAuthWorks() {
        //when: 'A secured URL is accessed with Basic Auth'
        HttpResponse<String> rsp = client.toBlocking().exchange(HttpRequest.GET("/")
                .accept(MediaType.TEXT_PLAIN)
                .basicAuth("sherlock", "password"), 
                String.class); 
        //then: 'the endpoint can be accessed'
        assertEquals(rsp.getStatus(),  HttpStatus.OK);
        assertEquals(rsp.getBody().get(),  "sherlock"); 
    }
}