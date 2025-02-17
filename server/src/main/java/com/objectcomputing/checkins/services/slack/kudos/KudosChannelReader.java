package com.objectcomputing.checkins.services.slack.kudos;

import com.objectcomputing.checkins.services.slack.SlackReader;
import com.objectcomputing.checkins.configuration.CheckInsConfiguration;

import com.slack.api.model.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Singleton
public class KudosChannelReader {
    private static final Logger LOG = LoggerFactory.getLogger(KudosChannelReader.class);

    @Inject
    private KudosChannelReadTimeStore kudosChannelReadTimeStore;

    @Inject
    private CheckInsConfiguration configuration;

    @Inject
    private SlackReader slackReader;

    @Inject
    private SlackKudosCreator slackKudosCreator;

    public void readChannel() {
        Optional<KudosChannelReadTime> readTime =
            kudosChannelReadTimeStore.findById(KudosChannelReadTime.key);
        boolean present = readTime.isPresent();
        LocalDateTime lastImport = present ? readTime.get().getReadTime()
                                           : LocalDateTime.now();

        String channelId = configuration.getApplication()
                                        .getSlack().getKudosChannel();
        LOG.info("Reading messages from " + channelId +
                 " as of " + lastImport.toString());
        List<Message> messages = slackReader.read(channelId, lastImport);
        if (present) {
            kudosChannelReadTimeStore.update(new KudosChannelReadTime());
        } else {
            kudosChannelReadTimeStore.save(new KudosChannelReadTime());
        }

        if (!messages.isEmpty()) {
            slackKudosCreator.store(messages);
        }
    }
}
