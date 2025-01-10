package com.objectcomputing.checkins.services.pulse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.Readable;
import io.micronaut.core.io.IOUtils;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

@Singleton
public class PulseSlackPoster {
    private static final Logger LOG = LoggerFactory.getLogger(PulseSlackPoster.class);

    @Inject
    private HttpClient slackClient;

    @Inject
    private CheckInsConfiguration configuration;

    @Value("classpath:slack/pulse_slack_blocks.json")
    private Readable pulseSlackBlocks;

    public void send() {
        String slackBlocks = getSlackBlocks();

        // See if we can have a webhook URL.
        String slackWebHook = configuration.getApplication()
                                           .getPulseResponse()
                                           .getSlack().getWebhookUrl();
        if (slackWebHook != null && !slackBlocks.isEmpty()) {
            // POST it to Slack.
            HttpRequest<String> request = HttpRequest.POST(slackWebHook,
                                                           slackBlocks);
            slackClient.exchange(request);
        }
    }

    private String getSlackBlocks() {
        try {
            return IOUtils.readText(
                       new BufferedReader(pulseSlackBlocks.asReader()));
                    
        } catch(Exception ex) {
          LOG.error(ex.toString());
          return "";
        }
    }
}

