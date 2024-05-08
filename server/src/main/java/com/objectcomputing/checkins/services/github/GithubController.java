package com.objectcomputing.checkins.services.github;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.reactivestreams.Publisher;


@Controller("/services/github-issue")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "github")
public class GithubController {

    private GithubClient githubClient;

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
    Publisher<IssueResponseDTO> sendIssue(@Body @Valid @NotNull IssueCreateDTO request) {
        return githubClient.sendIssue(request);
    }

}
