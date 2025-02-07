package com.objectcomputing.checkins.services.slack.kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.kudos.KudosCreateDTO;
import com.objectcomputing.checkins.services.kudos.KudosServices;

import jakarta.inject.Singleton;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void store(UUID automatedKudosId) {
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

    public void remove(UUID automatedKudosId) {
        automatedKudosRepository.deleteById(automatedKudosId);
    }
}
