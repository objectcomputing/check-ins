package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.slack.SlackReader;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

import com.slack.api.model.Message;

import jakarta.inject.Singleton;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Singleton
@Replaces(SlackReader.class)
@Requires(property = "replace.slackreader", value = StringUtils.TRUE)
public class SlackReaderReplacement extends SlackReader {
    public final Map<String, List<Message>> channelMessages = new HashMap<>();

    @Override
    public List<Message> read(String channelId, LocalDateTime last) {
        List<Message> messages = new ArrayList<>();
        if (channelMessages.containsKey(channelId)) {
            long ts = last.atZone(ZoneId.systemDefault())
                          .toInstant().getEpochSecond();
            for (Message message : channelMessages.get(channelId)) {
                long messageTime = Long.parseLong(message.getTs());
                if (messageTime >= ts) {
                    messages.add(message);
                }
            }
        }
        return messages;
    }

    public void addMessage(String channelId, String userId,
                           String text, LocalDateTime sendTime) {
        Message message = new Message();
        message.setTs(String.valueOf(sendTime.atZone(ZoneId.systemDefault())
                                             .toInstant().getEpochSecond()));
        message.setText(text);
        message.setUser(userId);

        if (!channelMessages.containsKey(channelId)) {
            channelMessages.put(channelId, new ArrayList<Message>());
        }
        channelMessages.get(channelId).add(message);
    }
}
