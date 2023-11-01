package com.objectcomputing.checkins.services.kudos.kudos_recipient;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Singleton;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Singleton
public class KudosRecipientServicesImpl implements KudosRecipientServices {

    private final KudosRecipientRepository kudosRecipientRepository;
    private final CurrentUserServices currentUserServices;
    private final KudosRepository kudosRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;

    public KudosRecipientServicesImpl(KudosRecipientRepository kudosRecipientRepository,
                                      CurrentUserServices currentUserServices,
                                      KudosRepository kudosRepository,
                                      MemberProfileRetrievalServices memberProfileRetrievalServices) {
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.currentUserServices = currentUserServices;
        this.kudosRepository = kudosRepository;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
    }

    @Override
    public KudosRecipient save(KudosRecipient kudosRecipient) {

        if (kudosRecipient.getId() != null) {
            throw new BadArgException("KudosRecipient id must be null");
        }

        Kudos kudos = kudosRepository.findById(kudosRecipient.getKudosId()).orElseThrow(() -> {
            throw new NotFoundException("No kudos with id %s", kudosRecipient.getKudosId());
        });

        boolean isKudosCreator = currentUserServices.getCurrentUser().getId().equals(kudos.getSenderId());
        boolean isAdmin = currentUserServices.isAdmin();
        if (!isAdmin && !isKudosCreator) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        MemberProfile member = memberProfileRetrievalServices.getById(kudosRecipient.getMemberId()).orElseThrow(() -> {
            throw new BadArgException("Cannot save KudosRecipient: member %s does not exist", kudosRecipient.getMemberId());
        });

        if (member.getTerminationDate() != null && member.getTerminationDate().isBefore(LocalDate.now())) {
            throw new BadArgException("Cannot save KudosRecipient for terminated member %s", kudosRecipient.getMemberId());
        }

        return kudosRecipientRepository.save(kudosRecipient);
    }

    @Override
    public List<KudosRecipient> getAllByKudosId(UUID kudosId) {
        return kudosRecipientRepository.findByKudosId(kudosId);
    }
}
