package com.objectcomputing.checkins.services.github;

import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.reactivestreams.Publisher;

@Controller("/services/github-issue")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "github")
class GithubController {

    private final GithubClient githubClient;

    GithubController(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    /**
     * Create and save a new github issue
     *
     * @param request, {@link IssueCreateDTO}
     * @return an {@link IssueResponseDTO}
     */
    @Post
    @SingleResult
    Publisher<IssueResponseDTO> sendIssue(@Body @Valid @NotNull IssueCreateDTO request) {
        return githubClient.sendIssue(request);
    }
}
