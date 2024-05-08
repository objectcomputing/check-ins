package com.objectcomputing.checkins.services.checkindocument;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class CheckinDocumentCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        CheckinDocumentCreateDTO dto = new CheckinDocumentCreateDTO();
        assertNull(dto.getCheckinsId());
        assertNull(dto.getUploadDocId());
    }

    @Test
    void testConstraintViolation() {
        CheckinDocumentCreateDTO dto = new CheckinDocumentCreateDTO();

        Set<ConstraintViolation<CheckinDocumentCreateDTO>> violations = validator.validate(dto);
        assertEquals(violations.size(), 2);
        for (ConstraintViolation<CheckinDocumentCreateDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        CheckinDocumentCreateDTO dto = new CheckinDocumentCreateDTO();

        UUID checkinsId = UUID.randomUUID();

        dto.setCheckinsId(checkinsId);
        assertEquals(dto.getCheckinsId(), checkinsId);

        dto.setUploadDocId("ExampleId");
        assertEquals("ExampleId", dto.getUploadDocId());

        Set<ConstraintViolation<CheckinDocumentCreateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
