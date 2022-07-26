package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Singleton;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import static io.micronaut.http.HttpRequest.POST;
import static io.netty.handler.codec.http.HttpHeaderNames.ACCEPT;

@Singleton
public class SignRequestClient {

    private String baseURL = "https://ocitest.signrequest.com/api/v1/";
    private final HttpClient httpClient;
    public static final String SIGNREQUEST_CREDENTIALS_TOKEN = "signrequest-credentials.signrequest_token";
    private final String signrequestToken;

    public SignRequestClient(HttpClient httpClient, @Property(name = SIGNREQUEST_CREDENTIALS_TOKEN) String signrequestToken) {
        this.httpClient = httpClient;
        this.signrequestToken = signrequestToken;
    }

    public String getDocuments() {
        try {
            String retrieve = httpClient.toBlocking()
                    .retrieve(HttpRequest.GET("/documents/")
                            .header("Authorization", SIGNREQUEST_CREDENTIALS_TOKEN));
            return retrieve;
        }
        catch (HttpClientResponseException e) {
            System.out.println(e.getResponse().reason());
            System.out.println(e.getResponse().body());
            return null;
        }
    }

    public String sendRequest(SignRequestCreateDTO requestDTO) {
        JSONObject jsonObj = new JSONObject(requestDTO);

        try {
            String req = httpClient.toBlocking().retrieve(POST(baseURL + "/signrequest-quick-create/", jsonObj)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", signrequestToken));
            return req;
        }
        catch (HttpClientResponseException e) {
            System.out.println(e.getResponse().reason());
            System.out.println(e.getResponse().body());
            return null;
        }
    }

//    public String embedSignRequest(SignRequestCreateDTO requestDTO) {
//        JSONObject jsonObj = new JSONObject(requestDTO);
//
//        try {
//            String req = httpClient.toBlocking().retrieve(POST("/signrequest-quick-create/"))
//        }
//    }

}
