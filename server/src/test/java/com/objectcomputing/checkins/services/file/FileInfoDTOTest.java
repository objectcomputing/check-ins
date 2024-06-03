package com.objectcomputing.checkins.services.file;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FileInfoDTOTest extends TestContainersSuite {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        FileInfoDTO dto = new FileInfoDTO();
        assertNull(dto.getFileId());
        assertNull(dto.getCheckInId());
        assertNull(dto.getSize());
        assertNull(dto.getName());
    }

    @Test
    void testConstraintViolation() {
        FileInfoDTO dto = new FileInfoDTO();

        Set<ConstraintViolation<FileInfoDTO>> violations = validator.validate(dto);
        assertEquals(4, violations.size());
        for (ConstraintViolation<FileInfoDTO> violation : violations) {
            assertEquals("must not be null", violation.getMessage());
        }
    }

    @Test
    void testPopulatedDTO() {
        FileInfoDTO dto = new FileInfoDTO();
        Long testSize = 1L;
        UUID testCheckinId = UUID.randomUUID();

        dto.setFileId("some.file.id");
        assertEquals("some.file.id", dto.getFileId());

        dto.setName("some.file.name");
        assertEquals("some.file.name", dto.getName());

        dto.setCheckInId(testCheckinId);
        assertEquals(dto.getCheckInId(), testCheckinId);

        dto.setSize(testSize);
        assertEquals(dto.getSize(), testSize);

        Set<ConstraintViolation<FileInfoDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
