package com.objectcomputing.checkins.services.github;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.AUTHORIZATION;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@ConfigurationProperties("github-credentials")
@Singleton
public class GithubClient {

    @NotNull
    public String github_token;
    private String baseURL = "https://api.github.com/";
    private final HttpClient httpClient;

    public GithubClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    Publisher<IssueResponseDTO> sendIssue(IssueCreateDTO requestDTO) {
        HttpRequest<?> req = HttpRequest.POST(baseURL + "repos/oci-labs/check-ins/issues", requestDTO)
                .header(USER_AGENT, "Micronaut HTTP Client")
                .header(ACCEPT, "application/vnd.github.v3+json, application/json")
                .header(AUTHORIZATION, "token " + github_token);

        return httpClient.retrieve(req, IssueResponseDTO.class);
    }
}
