package com.objectcomputing.checkins.services.github;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;

import static io.micronaut.http.HttpHeaders.*;

@Singleton
public class GithubClient {

    private String baseURL = "https://api.github.com/";
    private final HttpClient httpClient;

    public GithubClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    Publisher<IssueResponseDTO> sendIssue(IssueCreateDTO requestDTO) {
        HttpRequest<?> req = HttpRequest.POST(baseURL + "repos/oci-labs/check-ins/issues", requestDTO)
                .header(USER_AGENT, "Micronaut HTTP Client")
                .header(ACCEPT, "application/vnd.github.v3+json, application/json")
                .header(AUTHORIZATION, "token " + System.getenv("GITHUB_TOKEN"));

        return httpClient.retrieve(req, IssueResponseDTO.class);
    }

}
