package com.objectcomputing.checkins.services.kudos;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface KudosServices {

    Kudos save(KudosCreateDTO kudos);

    Kudos approve(Kudos kudos);

    KudosResponseDTO getById(UUID id);

    void delete(UUID id);

    List<KudosResponseDTO> findByValues(@Nullable UUID recipientId, @Nullable UUID senderId, @Nullable Boolean isPending, @Nullable Boolean isPublic);
}
