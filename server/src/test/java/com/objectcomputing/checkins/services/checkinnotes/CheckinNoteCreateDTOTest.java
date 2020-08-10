package com.objectcomputing.checkins.services.checkinnotes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.micronaut.test.annotation.MicronautTest;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckinNoteCreateDTOTest {
    
@Test
public void testPopulateDTO(){
    CheckinNoteCreateDTO checkinNoteCreateDTO = new CheckinNoteCreateDTO();
    UUID checkinid = UUID.randomUUID();
    UUID createdById = UUID.randomUUID();
    String description = "Test populated";
    boolean privateNotes = false;

    checkinNoteCreateDTO.setCheckinid(checkinid);
    checkinNoteCreateDTO.setCreatedbyid(createdById);
    checkinNoteCreateDTO.setDescription(description);

    assertEquals(checkinNoteCreateDTO.getCheckinid(), checkinid);
    assertEquals(checkinNoteCreateDTO.getCreatedbyid(),createdById);
    assertEquals(checkinNoteCreateDTO.getDescription(), description);
}
}