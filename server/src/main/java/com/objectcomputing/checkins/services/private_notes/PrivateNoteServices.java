package com.objectcomputing.checkins.services.private_notes;

import java.util.Set;
import java.util.UUID;

public interface PrivateNoteServices {

    PrivateNote save(PrivateNote privateNote);

    PrivateNote read(UUID id);

    PrivateNote update(PrivateNote privateNote);

}