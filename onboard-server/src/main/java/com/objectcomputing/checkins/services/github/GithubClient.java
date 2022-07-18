package com.objectcomputing.checkins.services.github;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import org.reactivestreams.Publisher;

import jakarta.inject.Singleton;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.AUTHORIZATION;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Singleton
@Requires(property = GithubClient.GITHUB_CREDENTIALS_TOKEN)
public class GithubClient {

    private String baseURL = "https://api.github.com/";
    private final HttpClient httpClient;
    public static final String GITHUB_CREDENTIALS_TOKEN = "github-credentials.github_token";
    private final String githubToken;

    public GithubClient(HttpClient httpClient, @Property(name = GITHUB_CREDENTIALS_TOKEN) String githubToken) {
        this.httpClient = httpClient;
        this.githubToken = githubToken;
    }

    Publisher<IssueResponseDTO> sendIssue(IssueCreateDTO requestDTO) {
        HttpRequest<?> req = HttpRequest.POST(baseURL + "repos/oci-labs/check-ins/issues", requestDTO)
                .header(USER_AGENT, "Micronaut HTTP Client")
                .header(ACCEPT, "application/vnd.github.v3+json, application/json")
                .header(AUTHORIZATION, "token " + githubToken);

        return httpClient.retrieve(req, IssueResponseDTO.class);
    }
}