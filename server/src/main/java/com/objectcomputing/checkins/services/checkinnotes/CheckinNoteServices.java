package com.objectcomputing.checkins.services.checkinnotes;

import java.util.Set;
import java.util.UUID;

public interface CheckinNoteServices {

    CheckinNote save(CheckinNote checkinNote);

    CheckinNote read(UUID id);

    Set<CheckinNote> readAll();

    CheckinNote update(CheckinNote checkinNote);

    Set<CheckinNote> findByFields(UUID checkinid, UUID createdbyid);

    void delete(UUID id);
}