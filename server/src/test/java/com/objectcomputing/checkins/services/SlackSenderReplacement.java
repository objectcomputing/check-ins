package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.notifications.social_media.SlackSender;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;

import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Singleton
@Replaces(SlackSender.class)
@Requires(property = "replace.slacksender", value = StringUtils.TRUE)
public class SlackSenderReplacement extends SlackSender {
    public final Map<String, List<String>> sent = new HashMap<>();

    public void reset() {
        sent.clear();
    }

    @Override
    public boolean send(List<String> userIds, String slackBlocks) {
        for (String userId : userIds) {
            if (!sent.containsKey(userId)) {
                sent.put(userId, new ArrayList<String>());
            }
            sent.get(userId).add(slackBlocks);
        }
        return true;
    }

    @Override
    public boolean send(String channelId, String slackBlocks) {
        if (!sent.containsKey(channelId)) {
            sent.put(channelId, new ArrayList<String>());
        }
        sent.get(channelId).add(slackBlocks);
        return true;
    }
}

