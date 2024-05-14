package com.objectcomputing.checkins.services.file;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class FileInfoDTOTest {

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
        assertEquals(violations.size(), 4);
        for (ConstraintViolation<FileInfoDTO> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testPopulatedDTO() {
        FileInfoDTO dto = new FileInfoDTO();
        Long testSize = 1L;
        UUID testCheckinId = UUID.randomUUID();

        dto.setFileId("some.file.id");
        assertEquals(dto.getFileId(), "some.file.id");

        dto.setName("some.file.name");
        assertEquals(dto.getName(), "some.file.name");

        dto.setCheckInId(testCheckinId);
        assertEquals(dto.getCheckInId(), testCheckinId);

        dto.setSize(testSize);
        assertEquals(dto.getSize(), testSize);

        Set<ConstraintViolation<FileInfoDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
