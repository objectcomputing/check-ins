package com.objectcomputing.checkins.notifications.social_media;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import java.util.List;

@Singleton
public class SlackPoster {
    @Inject
    private HttpClient slackClient;

    @Inject
    private CheckInsConfiguration configuration;

    public HttpResponse post(String slackBlock) {
        // See if we have a webhook URL.
        String slackWebHook = configuration.getApplication()
                                           .getSlack().getWebhookUrl();
        if (slackWebHook != null) {
            // POST it to Slack.
            BlockingHttpClient client = slackClient.toBlocking();
            HttpRequest<String> request = HttpRequest.POST(slackWebHook,
                                                           slackBlock);
            return client.exchange(request);
        }
        return HttpResponse.status(HttpStatus.GONE,
                                   "Slack Webhook URL is not configured");
    }
}

