package com.objectcomputing.checkins.services.slack;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import com.slack.api.Slack;
import com.slack.api.model.Message;
import com.slack.api.model.Conversation;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Singleton
public class SlackReader {
    private static final Logger LOG = LoggerFactory.getLogger(SlackReader.class);

    @Inject
    private CheckInsConfiguration configuration;

    public List<Message> read(String channelId, LocalDateTime last) {
        String token = configuration.getApplication().getSlack().getBotToken();
        if (token != null) {
            try {
                long ts = last.atZone(ZoneId.systemDefault())
                              .toInstant().getEpochSecond();
                String timestamp = String.valueOf(ts);
                MethodsClient client = Slack.getInstance().methods(token);
                ConversationsHistoryResponse response =
                    client.conversationsHistory(
                        ConversationsHistoryRequest.builder()
                                                   .channel(channelId)
                                                   .oldest(timestamp)
                                                   .inclusive(true)
                                                   .build());

                if (response.isOk()) {
                    return response.getMessages();
                } else {
                    LOG.error("Slack Response: " + response.getError() +
                              " - " + response.getNeeded());
                }
            } catch(IOException e) {
                LOG.error("SlackReader.read: " + e.toString());
            } catch(SlackApiException e) {
                LOG.error("SlackReader.read: " + e.toString());
            }
        } else {
            LOG.error("Slack Token not available");
        }
        return new ArrayList<Message>();
    }
}

