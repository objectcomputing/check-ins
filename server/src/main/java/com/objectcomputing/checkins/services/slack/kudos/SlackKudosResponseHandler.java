package com.objectcomputing.checkins.services.slack.kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.kudos.KudosCreateDTO;
import com.objectcomputing.checkins.services.kudos.KudosServices;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Singleton
public class SlackKudosResponseHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SlackKudosResponseHandler.class);

    @Inject
    private KudosServices kudosServices;

    @Inject
    private AutomatedKudosRepository automatedKudosRepository;

    @Inject
    private MemberProfileServices memberProfileServices;

    public boolean handle(Map<String, Object> map) {
        try {
            // Get the blocks out of the message so that we can grab the
            // automated kudos id.
            Map<String, Object> message =
                                    (Map<String, Object>)map.get("message");
            List<Object> blocks = (List<Object>)message.get("blocks");
            if (blocks.size() > 0) {
                Map<String, Object> first = (Map<String, Object>)blocks.get(0);
                String id = (String)first.get("block_id");
                UUID uuid = UUID.fromString(id);

                List<Object> actions = (List<Object>)map.get("actions");
                if (actions.size() > 0) {
                    Map<String, Object> entry =
                                            (Map<String, Object>)actions.get(0);
                    String actionId = (String)entry.get("action_id");
                    if (actionId.equals("yes_button")) {
                        store(uuid);
                    } else {
                        automatedKudosRepository.deleteById(uuid);
                    }
                    return true;
                }
            }
        } catch (Exception ex) {
            LOG.error("SlackKudosResponseHandler.handle: " + ex.toString());
        }
        return false;
    }

    private void store(UUID automatedKudosId) {
        Optional<AutomatedKudos> found =
            automatedKudosRepository.findById(automatedKudosId);
        if (found.isPresent()) {
            AutomatedKudos kudos = found.get();
            List<MemberProfile> recipients = new ArrayList<>();
            for (String recipientId : kudos.getRecipientIds()) {
                recipients.add(memberProfileServices.getById(
                                   UUID.fromString(recipientId)));
            }
            KudosCreateDTO dto =
                new KudosCreateDTO(kudos.getMessage(), kudos.getSenderId(),
                                   null, true, recipients);
            kudosServices.savePreapproved(dto);
            automatedKudosRepository.deleteById(automatedKudosId);
        } else {
            LOG.error("Unable to find automated kudos: " +
                      automatedKudosId.toString());
        }
    }
}
