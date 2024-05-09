package com.objectcomputing.checkins.services.pulseresponse;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PulseResponseTest {

    @Inject
    public Validator validator;

    @Test
    void testPulseResponseInstantiation() {
        LocalDate submissionDate= LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final String internalFeelings  = "exampleId";
        final String externalFeelings  = "exampleId2";
        PulseResponse pulseResponse = new PulseResponse(1, 2, submissionDate,teamMemberId, internalFeelings, externalFeelings);
        assertEquals(teamMemberId, pulseResponse.getTeamMemberId());
        assertEquals(internalFeelings , pulseResponse.getInternalFeelings ());
        assertEquals(externalFeelings , pulseResponse.getExternalFeelings ());
    }

    @Test
    void testConstraintViolation() {
        LocalDate submissionDate= LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final String internalFeelings  = "exampleId";
        final String externalFeelings  = "exampleId2";
        PulseResponse pulseResponse = new PulseResponse(1, 2, submissionDate, teamMemberId, internalFeelings,externalFeelings);

        pulseResponse.setInternalFeelings (null);
        pulseResponse.setExternalFeelings (null);

        Set<ConstraintViolation<PulseResponse>> violations = validator.validate(pulseResponse);
        assertEquals(2, violations.size());
        for (ConstraintViolation<PulseResponse> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final Integer internalScore = 1;
        final Integer externalScore = 2;
        LocalDate submissionDate= LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final String internalFeelings  = "exampleId";
        final String externalFeelings   = "exampleId2";

        PulseResponse pulseResponse1 = new PulseResponse(id,internalScore,externalScore,submissionDate,teamMemberId, internalFeelings, externalFeelings );
        PulseResponse pulseResponse2 = new PulseResponse(id,internalScore,externalScore,submissionDate,teamMemberId, internalFeelings, externalFeelings );
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setId(null);
        assertNotEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setId(pulseResponse1.getId());
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setInternalScore(pulseResponse1.getInternalScore());
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setExternalScore(pulseResponse1.getExternalScore());
        assertEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setInternalFeelings ("exampleId2");
        pulseResponse2.setExternalFeelings ("exampleId3");
        assertNotEquals(pulseResponse1, pulseResponse2);

        pulseResponse2.setInternalScore (3);
        pulseResponse2.setExternalScore (4);
        assertNotEquals(pulseResponse1, pulseResponse2);
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        LocalDate submissionDate= LocalDate.of(2019, 1, 1);
        final UUID teamMemberId = UUID.randomUUID();
        final Integer internalScore = 1;
        final Integer externalScore = 2;
        final String internalFeelings  = "exampleId";
        final String externalFeelings  = "exampleId2";
        PulseResponse pulseResponse = new PulseResponse(id,internalScore,externalScore,submissionDate, teamMemberId, internalFeelings, externalFeelings );

        String toString = pulseResponse.toString();
        assertTrue(toString.contains(teamMemberId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(internalFeelings ));
        assertTrue(toString.contains(externalFeelings ));
    }
}
