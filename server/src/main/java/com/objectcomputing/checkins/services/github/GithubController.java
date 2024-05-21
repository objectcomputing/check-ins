package com.objectcomputing.checkins.services.github;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;


@Controller("/services/github-issue")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "github")
public class GithubController {

    private final GithubClient githubClient;

    public GithubController(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    /**
     * Create and save a new github issue
     *
     * @param request, {@link IssueCreateDTO}
     * @return {@link HttpResponse < IssueResponseDTO >}
     */
    @Post
    Mono<IssueResponseDTO> sendIssue(@Body @Valid @NotNull IssueCreateDTO request) {
        return Mono.from(githubClient.sendIssue(request));
    }

}
