package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

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

}
