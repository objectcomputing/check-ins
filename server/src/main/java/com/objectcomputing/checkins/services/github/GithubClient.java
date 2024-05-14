package com.objectcomputing.checkins.services.github;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.scheduling.TaskExecutors;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;

import static io.micronaut.http.HttpHeaders.*;

@Singleton
@Requires(property = GithubClient.GITHUB_CREDENTIALS_TOKEN)
public class GithubClient {

    private String baseURL = "https://api.github.com/";
    private final HttpClient httpClient;
    public static final String GITHUB_CREDENTIALS_TOKEN = "github-credentials.github_token";
    private final String githubToken;
    private final Scheduler scheduler;

    public GithubClient(HttpClient httpClient, @Property(name = GITHUB_CREDENTIALS_TOKEN) String githubToken,
                        @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.httpClient = httpClient;
        this.githubToken = githubToken;
        this.scheduler = Schedulers.fromExecutorService(ioExecutorService);
    }

    Publisher<IssueResponseDTO> sendIssue(IssueCreateDTO requestDTO) {
        HttpRequest<?> req = HttpRequest.POST(baseURL + "repos/oci-labs/check-ins/issues", requestDTO)
                .header(USER_AGENT, "Micronaut HTTP Client")
                .header(ACCEPT, "application/vnd.github.v3+json, application/json")
                .header(AUTHORIZATION, "token " + githubToken);

        return Mono.from(httpClient.retrieve(req, IssueResponseDTO.class)).subscribeOn(scheduler);
    }
}
