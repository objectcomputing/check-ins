package com.objectcomputing.checkins.services.slack.kudos;

import com.objectcomputing.checkins.services.slack.SlackReader;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import com.slack.api.model.Message;

import io.micronaut.scheduling.annotation.Scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import java.util.List;
import java.time.LocalDateTime;

@Singleton
public class KudosChannelReader {
    private static final Logger LOG = LoggerFactory.getLogger(KudosChannelReader.class);

    private static LocalDateTime lastImport = null;

    @Inject
    private CheckInsConfiguration configuration;

    @Inject
    private SlackReader slackReader;

    @Inject
    private SlackKudosCreator slackKudosCreator;

    @Scheduled(fixedDelay = "1m")
    public void readChannel() {
        if (lastImport == null) {
            lastImport = LocalDateTime.now();
        }

        String channelId = configuration.getApplication()
                                        .getSlack().getKudosChannel();
        List<Message> messages = slackReader.read(channelId, lastImport);
        updateLastImportTime();

        slackKudosCreator.store(messages);
    }

    private LocalDateTime getLastImportTime() {
        return lastImport;
    }

    private void updateLastImportTime() {
        lastImport = LocalDateTime.now();
    }
}
