package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import org.json.JSONObject;
import org.reactivestreams.Publisher;

import static io.netty.handler.codec.http.HttpHeaderNames.ACCEPT;

@Singleton
public class SignRequestClient {

    private String baseURL = "https://ocitest.signrequest.com/api/v1/";
    private HttpClient httpClient;
    public static final String SIGNREQUEST_CREDENTIALS_TOKEN = "signrequest-credentials.signrequest_token";
    public String signrequestToken;

    public SignRequestClient(HttpClient httpClient, @Property(name = SIGNREQUEST_CREDENTIALS_TOKEN) String signrequestToken) {
        this.httpClient = httpClient;
        this.signrequestToken = signrequestToken;
    }

    Publisher<SignRequestResponseDTO> sendRequest(SignRequestCreateDTO requestDTO) {
        JSONObject jsonObj = new JSONObject(requestDTO);
        HttpRequest req = HttpRequest.POST(baseURL + "/signrequest-quick-create/", jsonObj)
                .contentType(MediaType.APPLICATION_JSON)
                //.header(ACCEPT, "application/json")
                .header("Authorization", signrequestToken);

        return httpClient.retrieve(req, SignRequestResponseDTO.class);
    }
}
