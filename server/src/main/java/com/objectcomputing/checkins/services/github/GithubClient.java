package com.objectcomputing.checkins.services.github;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.security.authentication.Authentication;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;

import static io.micronaut.http.HttpHeaders.*;

@Singleton
public class GithubClient {
    private String uri = "https://api.github.com/repos/oci-labs/check-ins/issues";
    private final HttpClient httpClient;

    public GithubClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void sendIssue(GithubRequestDTO requestDTO) {
        HttpRequest<?> req = HttpRequest.POST(uri, requestDTO)
                .header(ACCEPT, "application/vnd.github.v3+json, application/json")
                .header(AUTHORIZATION, "token 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");

        httpClient.retrieve(req);
    }




}
