package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@MicronautTest
public class SignRequestCreateDTOTest {

    @Inject
    private Validator validator;

    @Test
    void testDTOInstantiation() {
        SignRequestCreateDTO dto = new SignRequestCreateDTO();

        assertNull(dto.getEmail());
        assertNull(dto.getFile());
        assertNull(dto.getName());
        assertNull(dto.getFromEmail());
        assertNull(dto.getMessage());
        assertNull(dto.getSigningStatus());
        assertNull(dto.getSubject());
        assertNull(dto.getDeleteDays());
        assertNull(dto.getSigners());
    }

    @Test
    void testConstraintViolation() {
        SignRequestCreateDTO dto = new SignRequestCreateDTO();

        Set<ConstraintViolation<SignRequestCreateDTO>> violations = validator.validate(dto);
        // Think this should be 9, not 4
        assertEquals(violations.size(), 4);
        for (ConstraintViolation<SignRequestCreateDTO> violation: violations) {
            assertEquals(violation.getMessage(), "must not be blank");
        }
    }

    @Test
    void testPopulatedDTO() {
        SignRequestCreateDTO dto = new SignRequestCreateDTO();

        dto.setEmail("some.file.email");
        assertEquals(dto.getEmail(), "some.file.email");

        dto.setFile("some.file.file");
        assertEquals(dto.getFile(), "some.file.file");

        dto.setName("some.file.name");
        assertEquals(dto.getName(), "some.file.name");

        dto.setFromEmail("some.file.from.email");
        assertEquals(dto.getFromEmail(), "some.file.from.email");

        dto.setMessage("some.file.message");
        assertEquals(dto.getMessage(), "some.file.message");

        dto.setSigningStatus("some.file.signing.status");
        assertEquals(dto.getSigningStatus(), "some.file.signing.status");

        dto.setSubject("some.file.subject");
        assertEquals(dto.getSubject(), "some.file.subject");

        dto.setDeleteDays("some.file.delete.days");
        assertEquals(dto.getDeleteDays(), "some.file.delete.days");

        String[] signers = {"test"};
        dto.setSigners(signers);
        assertEquals(dto.getSigners(), signers);
    }

}
