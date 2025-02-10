package com.objectcomputing.checkins.services.slack.kudos;

import com.objectcomputing.checkins.notifications.social_media.SlackSearch;
import com.objectcomputing.checkins.notifications.social_media.SlackSender;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.exceptions.NotFoundException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.slack.api.model.Message;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.Readable;
import io.micronaut.core.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.StringBuffer;
import java.io.BufferedReader;

@Singleton
public class SlackKudosCreator {
    private static final Logger LOG = LoggerFactory.getLogger(SlackKudosCreator.class);

    @Inject
    private SlackSearch slackSearch;

    @Inject
    private SlackSender slackSender;

    @Inject
    private AutomatedKudosRepository automatedKudosRepository;

    @Inject
    private MemberProfileServices memberProfileServices;

    @Value("classpath:slack/kudos_slack_blocks.json")
    private Readable kudosSlackBlocks;

    public void store(List<Message> messages) {
        for (Message message : messages) {
            if (message.getSubtype() == null) {
                try {
                    AutomatedKudosDTO kudosDTO = createFromMessage(message);
                    if (kudosDTO.getRecipientIds().size() == 0) {
                        LOG.warn("Unable to extract recipients from message");
                        LOG.warn(message.getText());
                    } else {
                        automatedKudosRepository.save(
                                        new AutomatedKudos(kudosDTO));
                    }
                } catch (Exception ex) {
                    LOG.error("store: " + ex.toString());
                }
            } else {
                LOG.info("Skipping message: " + message.getText());
            }
        }

        requestAction();
    }

    private AutomatedKudosDTO createFromMessage(Message message) {
        String userId = message.getUser();
        MemberProfile sender = lookupUser(userId);
        List<UUID> recipients = new ArrayList<>();
        String text = processText(message.getText(), recipients);
        return new AutomatedKudosDTO(text, userId, sender.getId(), recipients);
    }

    private MemberProfile lookupUser(String userId) {
        if (userId == null) {
            throw new NotFoundException("User Id is not present");
        }

        String email = slackSearch.findUserEmail(userId);
        if (email == null) {
            throw new NotFoundException(
                          "Could not find an email address for " + userId);
        }
        return memberProfileServices.findByWorkEmail(email);
    }

    private String processText(String text, List<UUID> recipients) {
        // First, process user references.
        StringBuffer buffer = new StringBuffer(text.length());
        Pattern userRef = Pattern.compile("<@([^>]+)>");
        Matcher action = userRef.matcher(StringEscapeUtils.unescapeHtml4(text));
        while (action.find()) {
            // Pull out the recipient user id, get the profile and add it to
            // the list of recipients.
            String userId = action.group(1);
            MemberProfile profile = lookupUser(userId);
            recipients.add(profile.getId());

            // Replace the user reference with their full name.
            action.appendReplacement(buffer, Matcher.quoteReplacement(
                                     MemberProfileUtils.getFullName(profile)));
        }
        action.appendTail(buffer);
        text = buffer.toString();

        // Next, translate channel references to channel names.
        Pattern channelRef = Pattern.compile("<#([^>]+)\\|>");
        buffer = new StringBuffer(text.length());
        action = channelRef.matcher(text);
        while (action.find()) {
            // Get the name of the channel.
            String channelId = action.group(1);
            String name = slackSearch.findChannelName(channelId);
            if (name == null) {
                name = "unknown_channel";
            }
            name = "#" + name;

            // Replace the channel reference with the channel name.
            action.appendReplacement(buffer, Matcher.quoteReplacement(name));
        }
        action.appendTail(buffer);
        return buffer.toString();
    }

    private void requestAction() {
        for (AutomatedKudos kudos : automatedKudosRepository.getUnrequested()) {
            try {
                // Create the slack blocks, inserting the kudos UUID as the
                // block id.
                String blocks = getSlackBlocks(kudos.getId().toString(),
                                               kudos.getMessage());

                // Send the message to the sender of the kudos
                List<String> userIds = new ArrayList<>();
                userIds.add(kudos.getExternalId());
                if (slackSender.send(userIds, blocks)) {
                    // If the message was sent, set the requested flag and
                    // update the repository.
                    kudos.setRequested(true);
                    automatedKudosRepository.update(kudos);
                }
            } catch (Exception ex) {
                LOG.error("requestAction: " + ex.toString());
            }
        }
    }

    private String getSlackBlocks(String kudosUUID, String contents) {
        try {
            return String.format(IOUtils.readText(
                       new BufferedReader(kudosSlackBlocks.asReader())),
                       kudosUUID, StringEscapeUtils.escapeJson(contents));
        } catch(Exception ex) {
            LOG.error("SlackKudosCreator.getSlackBlocks: " + ex.toString());
            return "";
        }
    }
}
