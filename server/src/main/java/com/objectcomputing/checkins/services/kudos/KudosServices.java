package com.objectcomputing.checkins.services.kudos;

import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KudosServices {

    Kudos save(Kudos kudos);

    Kudos update(Kudos kudos);

    KudosResponseDTO getById(UUID id);

    Optional<Kudos> findById(UUID id);

    boolean delete(UUID id);

    List<KudosResponseDTO> findByValues(@Nullable UUID recipientId, @Nullable UUID senderId, @Nullable Boolean isPending);

}
