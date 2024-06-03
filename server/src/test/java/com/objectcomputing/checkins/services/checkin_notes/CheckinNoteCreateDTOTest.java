package com.objectcomputing.checkins.services.checkin_notes;

import com.objectcomputing.checkins.services.TestContainersSuite;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckinNoteCreateDTOTest extends TestContainersSuite {

    @Test
    void testPopulateDTO() {
        CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
        UUID checkinid = UUID.randomUUID();
        UUID createdById = UUID.randomUUID();
        String description = "Test populated";

        checkinNoteCreateDTO.setCheckinid(checkinid);
        checkinNoteCreateDTO.setCreatedbyid(createdById);
        checkinNoteCreateDTO.setDescription(description);

        assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinid);
        assertEquals(checkinNoteCreateDTO.getCreatedbyid(), createdById);
        assertEquals(checkinNoteCreateDTO.getDescription(), description);
    }
}