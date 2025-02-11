package com.objectcomputing.checkins.notifications.social_media;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.request.conversations.ConversationsOpenRequest;
import com.slack.api.methods.response.conversations.ConversationsOpenResponse;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
public class SlackSender {
    private static final Logger LOG = LoggerFactory.getLogger(SlackSender.class);

    @Inject
    private CheckInsConfiguration configuration;

    public boolean send(List<String> userIds, String slackBlocks) {
        // See if we have a token.
        String token = configuration.getApplication()
                                    .getSlack().getBotToken();
        if (token != null && !slackBlocks.isEmpty()) {
            MethodsClient client = Slack.getInstance().methods(token);

            try {
                ConversationsOpenResponse openResponse =
                    client.conversationsOpen(ConversationsOpenRequest.builder()
                        .users(userIds)
                        .returnIm(true)
                        .build());
                if (!openResponse.isOk()) {
                    LOG.error("Unable to open the conversation");
                    return false;
                }

                ChatPostMessageRequest request = ChatPostMessageRequest
                    .builder()
                    .channel(openResponse.getChannel().getId())
                    .blocksAsString(slackBlocks)
                    .build();

                // Send it to Slack
                ChatPostMessageResponse response = client.chatPostMessage(request);

                if (!response.isOk()) {
                    LOG.error("Unable to send the chat message: " +
                              response.getError());
                }

                return response.isOk();
            } catch(Exception ex) {
                LOG.error("SlackSender.send: " + ex.toString());
                return false;
            }
        } else {
            LOG.error("Missing token or missing slack blocks");
            return false;
        }
    }
}

