package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.views.ViewsOpenRequest;
import com.slack.api.methods.response.views.ViewsOpenResponse;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.Readable;
import io.micronaut.core.io.IOUtils;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.util.List;

@Singleton
public class PulseSlackCommand {
    private static final Logger LOG = LoggerFactory.getLogger(PulseSlackCommand.class);

    @Inject
    private CheckInsConfiguration configuration;

    @Value("classpath:slack/pulse_slack_blocks.json")
    private Readable pulseSlackBlocks;

    public boolean send(String triggerId) {
        String slackBlocks = getSlackBlocks();

        // See if we can have a token.
        String token = configuration.getApplication()
                                    .getPulseResponse()
                                    .getSlack().getBotToken();
        if (token != null && !slackBlocks.isEmpty()) {
            MethodsClient client = Slack.getInstance().methods(token);

            try {
                ViewsOpenRequest request = ViewsOpenRequest.builder()
                    .triggerId(triggerId)
                    .viewAsString(slackBlocks)
                    .build();

                // Send it to Slack
                ViewsOpenResponse response = client.viewsOpen(request);

                if (!response.isOk()) {
                    LOG.error("Unable to open the Pulse view");
                }

                return response.isOk();
            } catch(Exception ex) {
                LOG.error(ex.toString());
                return false;
            }
        } else {
            LOG.error("Missing token or missing slack blocks");
            return false;
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

