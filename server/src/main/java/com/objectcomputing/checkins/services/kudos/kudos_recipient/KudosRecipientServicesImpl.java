package com.objectcomputing.checkins.services.kudos.kudos_recipient;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.kudos.KudosServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

@Singleton
public class KudosRecipientServicesImpl implements KudosRecipientServices {

    private final KudosRecipientRepository kudosRecipientRepository;
    private final CurrentUserServices currentUserServices;
    private final KudosServices kudosServices;

    public KudosRecipientServicesImpl(KudosRecipientRepository kudosRecipientRepository,
                                      CurrentUserServices currentUserServices,
                                      KudosServices kudosServices) {
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.currentUserServices = currentUserServices;
        this.kudosServices = kudosServices;
    }

    @Override
    public KudosRecipient save(KudosRecipient kudosRecipient) {

        if (kudosRecipient.getId() != null) {
            throw new BadArgException("KudosRecipient id must be null");
        }

        Kudos kudos = kudosServices.findById(kudosRecipient.getKudosId()).orElseThrow(() -> {
            throw new NotFoundException("No kudos with id %s", kudosRecipient.getKudosId());
        });

        boolean isKudosCreator = currentUserServices.getCurrentUser().getId().equals(kudos.getSenderId());
        boolean isAdmin = currentUserServices.isAdmin();
        if (!isAdmin && !isKudosCreator) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (kudosRecipient.getTeamId() != null && kudosRecipient.getMemberId() != null) {
            throw new BadArgException("Cannot define both a team and a member in the same KudosRecipient");
        } else if (kudosRecipient.getTeamId() == null && kudosRecipient.getMemberId() == null) {
            throw new BadArgException("KudosRecipient must have either teamId or memberId defined");
        }

        // TODO: Throw exception if this kudos already has a team attached to it

        return kudosRecipientRepository.save(kudosRecipient);
    }

    @Override
    public List<KudosRecipient> getAllByKudosId(UUID kudosId) {
        return null;
    }
}
