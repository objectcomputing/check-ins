package com.objectcomputing.checkins.services.slack;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@Controller("/services/slack")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "slack")
public class SlackController {

    private final SlackSearch slackSearch;

    public SlackController(SlackSearch slackSearch) {
        this.slackSearch = slackSearch;
    }

    @Get("/emoji")
    public HttpResponse<Map<String, String>> customEmoji() {
        Map<String, String> customEmoji = slackSearch.getCustomEmoji();

        return HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create("/services/slack/emoji")))
                .body(customEmoji);
    }

    protected URI location(UUID uuid) {
        return URI.create("/services/slack/" + uuid);
    }
}
