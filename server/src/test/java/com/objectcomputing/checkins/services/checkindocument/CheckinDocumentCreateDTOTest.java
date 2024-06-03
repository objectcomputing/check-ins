package com.objectcomputing.checkins.services.checkindocument;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CheckinDocumentCreateDTOTest extends TestContainersSuite {

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
            assertEquals("must not be null", violation.getMessage());
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
