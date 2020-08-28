package com.objectcomputing.checkins.services.checkin_notes;

import java.util.Set;
import java.util.UUID;

public interface CheckinNoteServices {

    CheckinNote save(CheckinNote checkinNote);

    CheckinNote read(UUID id);

    CheckinNote update(CheckinNote checkinNote);

    Set<CheckinNote> findByFields(UUID checkinid, UUID createdbyid);
}