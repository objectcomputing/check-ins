package com.objectcomputing.checkins.services.github;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.AUTHORIZATION;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Singleton
@Requires(bean = GithubConfig.class)
class GithubClient {

    private final HttpClient httpClient;
    private final String githubToken;
    private final String baseURL;

    GithubClient(
            HttpClient httpClient,
            GithubConfig githubConfig
    ) {
        this.httpClient = httpClient;
        this.githubToken = githubConfig.getGithubToken();
        this.baseURL = githubConfig.getGithubUrl();
    }

    Publisher<IssueResponseDTO> sendIssue(IssueCreateDTO requestDTO) {
        HttpRequest<?> req = HttpRequest.POST(baseURL + "repos/objectcomputing/check-ins/issues", requestDTO)
                .header(USER_AGENT, "Micronaut HTTP Client")
                .header(ACCEPT, "application/vnd.github.v3+json, application/json")
                .header(AUTHORIZATION, "token " + githubToken);

        return httpClient.retrieve(req, IssueResponseDTO.class);
    }
}
