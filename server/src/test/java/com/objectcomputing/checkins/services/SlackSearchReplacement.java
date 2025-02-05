package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.notifications.social_media.SlackSearch;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Replaces(SlackSearch.class)
@Requires(property = "replace.slacksearch", value = StringUtils.TRUE)
public class SlackSearchReplacement extends SlackSearch {
    public final Map<String, String> channels = new HashMap<>();
    public final Map<String, String> users = new HashMap<>();

    public SlackSearchReplacement(CheckInsConfiguration checkInsConfiguration) {
        super(checkInsConfiguration);
    }

    @Override
    public String findChannelId(String channelName) {
        return channels.containsKey(channelName) ?
                   channels.get(channelName) : null;
    }

    @Override
    public String findUserEmail(String userId) {
        return users.containsKey(userId) ? users.get(userId) : null;
    }

    @Override
    public String findUserId(String userEmail) {
        for (Map.Entry<String, String> entry : users.entrySet()) {
            if (entry.getValue().equals(userEmail)) {
                return entry.getKey();
            }
        }
        return null;
    }
}

