package com.objectcomputing.checkins.services.agenda_item;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class AgendaItemCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        AgendaItemCreateDTO dto = new AgendaItemCreateDTO();
        assertNull(dto.getCheckinid());
        assertNull(dto.getCreatedbyid());
        assertNull(dto.getDescription());
    }

    @Test
    void testConstraintViolation() {
        AgendaItemCreateDTO dto = new AgendaItemCreateDTO();

        Set<ConstraintViolation<AgendaItemCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<AgendaItemCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        AgendaItemCreateDTO dto = new AgendaItemCreateDTO();

        UUID checkinId = UUID.randomUUID();
        UUID createById = UUID.randomUUID();

        dto.setCheckinid(checkinId);
        assertEquals(dto.getCheckinid(), checkinId);

        dto.setCreatedbyid(createById);
        assertEquals(dto.getCreatedbyid(), createById);

        dto.setDescription("DNC");
        assertEquals("DNC", dto.getDescription());

        Set<ConstraintViolation<AgendaItemCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
