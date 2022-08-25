package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.UUID;

@Singleton
public class KudosServicesImpl implements KudosServices {

    private final KudosRepository kudosRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final CurrentUserServices currentUserServices;

    public KudosServicesImpl(KudosRepository kudosRepository,
                             MemberProfileRetrievalServices memberProfileRetrievalServices,
                             CurrentUserServices currentUserServices) {
        this.kudosRepository = kudosRepository;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Kudos save(Kudos kudos) {

        if (kudos.getId() != null) {
            throw new BadArgException("Kudos id must be null");
        }

        memberProfileRetrievalServices.getById(kudos.getSenderId()).orElseThrow(() -> {
            throw new BadArgException("Kudos sender %s does not exist", kudos.getSenderId());
        });

        memberProfileRetrievalServices.getById(kudos.getRecipientId()).orElseThrow(() -> {
            throw new BadArgException("Kudos recipient %s does not exist", kudos.getRecipientId());
        });

        if (kudos.getSenderId().equals(kudos.getRecipientId())) {
            throw new BadArgException("Users cannot give themselves kudos");
        }

        return kudosRepository.save(kudos);
    }

    @Override
    public Kudos update(Kudos kudos) {

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to perform this operation");
        }

        if (kudos.getId() == null) {
            throw new BadArgException("Kudos id must not be null");
        }

        Kudos existingKudos = kudosRepository.findById(kudos.getId()).orElseThrow(() -> {
            throw new BadArgException("Kudos with id %s does not exist", kudos.getId());
        });

        if (!kudos.getSenderId().equals(existingKudos.getSenderId())) {
            throw new BadArgException("Cannot change kudos sender");
        }

        if (!kudos.getRecipientId().equals(existingKudos.getRecipientId())) {
            throw new BadArgException("Cannot change kudos recipient");
        }

        return kudosRepository.update(kudos);
    }

    @Override
    public Kudos getById(UUID id) {

        Kudos kudos = kudosRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Kudos with id %s does not exist", id);
        });

        boolean isSender = currentUserServices.getCurrentUser().getId().equals(kudos.getSenderId());
        if (kudos.getDateApproved() == null) {
            // If not yet approved, only admins and the sender can access the kudos
            if (!currentUserServices.isAdmin() && !isSender) {
                throw new PermissionException("You are not authorized to perform this operation");
            }
        } else {
            // If approved, admins, the sender, and the recipient can access the kudos
            boolean isRecipient = currentUserServices.getCurrentUser().getId().equals(kudos.getRecipientId());
            if (!currentUserServices.isAdmin() && !isSender && !isRecipient) {
                throw new PermissionException("You are not authorized to perform this operation");
            }
        }

        return kudos;
    }

    @Override
    public boolean delete(UUID id) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        kudosRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Kudos> findByValues(@Nullable UUID senderId, @Nullable UUID recipientId, @Nullable Boolean includePending) {
        boolean isAdmin = currentUserServices.isAdmin();

        // Do not include pending kudos by default
        if (includePending == null) {
            includePending = false;
        }

        if (senderId == null && recipientId == null && !isAdmin) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (senderId != null) {
            boolean isSender = currentUserServices.getCurrentUser().getId().equals(senderId);
            if (!isAdmin && !isSender) {
                throw new PermissionException("You are not authorized to do this operation");
            }
        }
        if (recipientId != null) {
            boolean isRecipient = currentUserServices.getCurrentUser().getId().equals(recipientId);
            if (!isAdmin && !isRecipient) {
                throw new PermissionException("You are not authorized to do this operation");
            }

            if (!isAdmin && includePending) {
                throw new PermissionException("You are not authorized to get pending kudos");
            }
        }

        String senderIdString = Util.nullSafeUUIDToString(senderId);
        String recipientIdString = Util.nullSafeUUIDToString(recipientId);

        return kudosRepository.search(senderIdString, recipientIdString, includePending);
    }
}
