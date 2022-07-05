package com.objectcomputing.checkins.services.checkindocument;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CheckinDocumentTest {

    @Inject
    private Validator validator;

    @Test
    void testCheckinDocumentInstantiation() {
        final UUID checkinsId = UUID.randomUUID();
        final String uploadDocId = "exampleId";
        CheckinDocument checkinDocument = new CheckinDocument(checkinsId, uploadDocId);
        assertEquals(checkinsId, checkinDocument.getCheckinsId());
        assertEquals(uploadDocId, checkinDocument.getUploadDocId());
    }

    @Test
    void testConstraintViolation() {
        final UUID checkinsId = UUID.randomUUID();
        final String uploadDocId = "exampleId";
        CheckinDocument checkinDocument = new CheckinDocument(checkinsId, uploadDocId);

        checkinDocument.setUploadDocId(null);

        Set<ConstraintViolation<CheckinDocument>> violations = validator.validate(checkinDocument);
        assertEquals(1, violations.size());
        for (ConstraintViolation<CheckinDocument> violation : violations) {
            assertEquals(violation.getMessage(), "must not be null");
        }
    }

    @Test
    void testEquals() {
        final UUID id = UUID.randomUUID();
        final UUID checkinsId = UUID.randomUUID();
        final String uploadDocId = "exampleId";

        CheckinDocument checkinDocument1 = new CheckinDocument(id, checkinsId, uploadDocId);
        CheckinDocument checkinDocument2 = new CheckinDocument(id, checkinsId, uploadDocId);
        assertEquals(checkinDocument1, checkinDocument2);

        checkinDocument2.setId(null);
        assertNotEquals(checkinDocument1, checkinDocument2);

        checkinDocument2.setId(checkinDocument1.getId());
        assertEquals(checkinDocument1, checkinDocument2);

        checkinDocument2.setUploadDocId("exampleId2");
        assertNotEquals(checkinDocument1, checkinDocument2);
    }

    @Test
    void testToString() {
        final UUID id = UUID.randomUUID();
        final UUID checkinsId = UUID.randomUUID();
        final String uploadDocId = "exampleId";
        CheckinDocument checkinDocument = new CheckinDocument(id, checkinsId, uploadDocId);

        String toString = checkinDocument.toString();
        assertTrue(toString.contains(checkinsId.toString()));
        assertTrue(toString.contains(id.toString()));
        assertTrue(toString.contains(uploadDocId));
    }
}
