package com.objectcomputing.checkins.services.github;

import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.core.async.annotation.SingleResult;

import javax.validation.Valid;


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
     * @param request, {@link GithubRequestDTO}
     * @return {@link HttpResponse < GithubRequestDTO >}
     */

    @Post()
    @SingleResult
    HttpResponse sendIssueToGithub(@Body @Valid GithubRequestDTO request) {
        githubClient.sendIssue(request);
        return HttpResponse.ok();
    }



}
