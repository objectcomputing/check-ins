package com.objectcomputing.checkins.services.kudos;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KudosServices {

    Kudos save(Kudos kudos);

    Kudos update(Kudos kudos);

    Kudos getById(UUID id);

    Optional<Kudos> findById(UUID id);

    boolean delete(UUID id);

    List<Kudos> findByValues(@Nullable UUID senderId, @Nullable UUID recipientId, @Nullable Boolean includePending);
}
