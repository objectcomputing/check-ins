package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface KudosFixture extends RepositoryFixture, TeamFixture {

    default Kudos createADefaultKudos(UUID senderId) {
        Kudos kudos = new Kudos();
        kudos.setMessage("Default Kudos");
        kudos.setSenderId(senderId);
        kudos.setPubliclyVisible(false);
        kudos.setDateCreated(LocalDate.now());

        return getKudosRepository().save(kudos);
    }

    default Kudos createPublicKudos(UUID senderId) {
        Kudos kudos = new Kudos();
        kudos.setMessage("Default Kudos");
        kudos.setSenderId(senderId);
        kudos.setPubliclyVisible(true);
        kudos.setDateCreated(LocalDate.now());

        return getKudosRepository().save(kudos);
    }

    default Kudos createApprovedKudos(UUID senderId) {
        Kudos kudos = new Kudos();
        kudos.setMessage("Default Kudos");
        kudos.setSenderId(senderId);
        kudos.setPubliclyVisible(true);
        kudos.setDateCreated(LocalDate.now());
        kudos.setDateApproved(LocalDate.now());

        return getKudosRepository().save(kudos);
    }

    default KudosRecipient createKudosRecipient(UUID kudosId, UUID memberId) {
        KudosRecipient kudosRecipient = new KudosRecipient(kudosId, memberId);

        return getKudosRecipientRepository().save(kudosRecipient);
    }

    default List<KudosRecipient> findKudosRecipientByKudosId(UUID kudosId) {
        return getKudosRecipientRepository().findByKudosId(kudosId);
    }
}
