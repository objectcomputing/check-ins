package com.objectcomputing.checkins.services.slack.kudos;

import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.slack.SlackReader;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import com.slack.api.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import java.util.List;
import java.time.LocalDateTime;

@Singleton
public class BotSentKudosLocator {
    private static final Logger LOG = LoggerFactory.getLogger(BotSentKudosLocator.class);

    @Inject
    private CheckInsConfiguration configuration;

    @Inject
    private SlackReader slackReader;

    // The identifiers needed to identify a message is the channel id and the
    // time stamp.  We are always looking at a specific channel.  So if we find
    // a message, we will return the timestamp as a string.  Otherwise, we will
    // return null.
    public String find(Kudos kudos) {
        String channelId = configuration.getApplication()
                                        .getSlack().getKudosChannel();
        List<Message> messages =
            slackReader.read(channelId, kudos.getDateCreated().atStartOfDay());

        String kudosText = kudos.getMessage().trim();
        for (Message message : messages) {
            // We only care about messages sent by our bot.
            if (message.getBotId() != null) {
                // The first line is the "kudos from" line and is not part of
                // the kudos message.
                int cut = message.getText().indexOf("\n");
                if (cut >= 0) {
                    String actual = message.getText().substring(cut + 1).trim();
                    if (actual.equals(kudosText)) {
                        return message.getTs();
                    }
                }
            }
        }
        return null;
    }
}
