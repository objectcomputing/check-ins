package com.objectcomputing.checkins.services.slack;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.model.Conversation;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.ConversationsListRequest;
import com.slack.api.methods.response.conversations.ConversationsListResponse;
import com.slack.api.methods.request.users.UsersLookupByEmailRequest;
import com.slack.api.methods.response.users.UsersLookupByEmailResponse;
import com.slack.api.methods.request.users.UsersInfoRequest;
import com.slack.api.methods.response.users.UsersInfoResponse;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import java.util.List;
import java.io.IOException;

import jnr.ffi.annotations.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class SlackSearch {
    private static final Logger LOG = LoggerFactory.getLogger(SlackSearch.class);

    private CheckInsConfiguration configuration;

    public SlackSearch(CheckInsConfiguration checkInsConfiguration) {
        this.configuration = checkInsConfiguration;
    }

    public String findChannelId(String channelName) {
        String token = configuration.getApplication().getSlack().getBotToken();
        if (token != null) {
            try {
                MethodsClient client = Slack.getInstance().methods(token);
                ConversationsListResponse response = client.conversationsList(
                    ConversationsListRequest.builder().build()
                );

                if (response.isOk()) {
                    for (Conversation conversation: response.getChannels()) {
                        if (conversation.getName().equals(channelName)) {
                            return conversation.getId();
                        }
                    }
                }
            } catch(IOException e) {
                LOG.error("SlackSearch.findChannelId: " + e.toString());
            } catch(SlackApiException e) {
                LOG.error("SlackSearch.findChannelId: " + e.toString());
            }
        }
        return null;
    }

    public String findChannelName(String channelId) {
        String token = configuration.getApplication().getSlack().getBotToken();
        if (token != null) {
            try {
                MethodsClient client = Slack.getInstance().methods(token);
                ConversationsListResponse response = client.conversationsList(
                    ConversationsListRequest.builder().build()
                );

                if (response.isOk()) {
                    for (Conversation conversation: response.getChannels()) {
                        if (conversation.getId().equals(channelId)) {
                            return conversation.getName();
                        }
                    }
                }
            } catch(IOException e) {
                LOG.error("SlackSearch.findChannelName: " + e.toString());
            } catch(SlackApiException e) {
                LOG.error("SlackSearch.findChannelName: " + e.toString());
            }
        }
        return null;
    }

    public String findUserId(String userEmail) {
        String token = configuration.getApplication().getSlack().getBotToken();
        if (token != null) {
            try {
                MethodsClient client = Slack.getInstance().methods(token);
                UsersLookupByEmailResponse response = client.usersLookupByEmail(
                    UsersLookupByEmailRequest.builder().email(userEmail).build()
                );

                if (response.isOk()) {
                    return response.getUser().getId();
                }
            } catch(IOException e) {
                LOG.error("SlackSearch.findUserId: " + e.toString());
            } catch(SlackApiException e) {
                LOG.error("SlackSearch.findUserId: " + e.toString());
            }
        }
        return null;
    }

    public String findUserEmail(String userId) {
        String token = configuration.getApplication().getSlack().getBotToken();
        if (token != null) {
            try {
                MethodsClient client = Slack.getInstance().methods(token);
                UsersInfoResponse response = client.usersInfo(
                    UsersInfoRequest.builder().user(userId).build()
                );

                if (response.isOk()) {
                    return response.getUser().getProfile().getEmail();
                }
            } catch(IOException e) {
                LOG.error("SlackSearch.findUserEmail: " + e.toString());
            } catch(SlackApiException e) {
                LOG.error("SlackSearch.findUserEmail: " + e.toString());
            }
        }
        return null;
    }
}

