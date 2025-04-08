package com.objectcomputing.checkins.services.slack;

import io.micronaut.cache.CacheConfiguration;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static io.micronaut.http.HttpHeaders.CACHE_CONTROL;

@Controller("/services/slack")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "slack")
public class SlackController {

    private final SlackSearch slackSearch;
    private final long expiry;

    public SlackController(@Named("slack-cache") CacheConfiguration cacheConfiguration, SlackSearch slackSearch) {
        // If un-configured, default to 1 hour
        this.expiry = cacheConfiguration.getExpireAfterWrite()
                .map(Duration::toSeconds)
                .orElseGet(() -> Duration.ofHours(1).toSeconds());
        this.slackSearch = slackSearch;
    }

    @Get("/emoji")
    public HttpResponse<Map<String, String>> customEmoji() {
        Map<String, String> customEmoji = slackSearch.getCustomEmoji();

        return HttpResponse
                .ok().header(CACHE_CONTROL, "public, max-age=%d".formatted(expiry))
                .headers(headers -> headers.location(URI.create("/services/slack/emoji")))
                .body(customEmoji);
    }
}
