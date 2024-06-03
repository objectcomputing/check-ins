package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PulseResponseCreateDTOTest extends TestContainersSuite {

    @Inject
    protected Validator validator;

    @Test
    void testDTOInstantiation() {
        PulseResponseCreateDTO dto = new PulseResponseCreateDTO();
        assertNull(dto.getInternalScore());
        assertNull(dto.getExternalScore());
        assertNull(dto.getSubmissionDate());
        assertNull(dto.getTeamMemberId());
        assertNull(dto.getInternalFeelings());
        assertNull(dto.getExternalFeelings());
    }

    @Test
    void testConstraintViolation() {
        PulseResponseCreateDTO dto = new PulseResponseCreateDTO();

        Set<ConstraintViolation<PulseResponseCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 3);
        for (ConstraintViolation<PulseResponseCreateDTO> violation : violations) {
            assertEquals("must not be null", violation.getMessage());
        }
    }

    @Test
    void testPopulatedDTO() {
        PulseResponseCreateDTO dto = new PulseResponseCreateDTO();

        UUID teamMemberId = UUID.randomUUID();

        dto.setInternalScore(1);
        assertEquals(1, dto.getInternalScore());

        dto.setExternalScore(2);
        assertEquals(2, dto.getExternalScore());

        dto.setSubmissionDate(LocalDate.of(2019, 1, 1));
        assertEquals(dto.getSubmissionDate(), LocalDate.of(2019, 1, 1));

        dto.setTeamMemberId(teamMemberId);
        assertEquals(dto.getTeamMemberId(), teamMemberId);

        dto.setInternalFeelings("ExampleId");
        assertEquals("ExampleId", dto.getInternalFeelings());

        dto.setExternalFeelings("ExampleId");
        assertEquals("ExampleId", dto.getExternalFeelings());

        Set<ConstraintViolation<PulseResponseCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
