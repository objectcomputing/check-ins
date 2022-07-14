package com.objectcomputing.checkins.services.private_notes;

import io.micronaut.core.annotation.Nullable;
import java.util.Set;
import java.util.UUID;

public interface PrivateNoteServices {

    PrivateNote save(PrivateNote privateNote);

    PrivateNote read(UUID id);

    PrivateNote update(PrivateNote privateNote);

    Set<PrivateNote> findByFields(@Nullable UUID checkinid, @Nullable UUID createbyid);

    }