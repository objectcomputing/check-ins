package com.objectcomputing.checkins.services.checkindocument;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckinDocumentBadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        CheckinDocumentBadArgException argException = new CheckinDocumentBadArgException(message);
        assertEquals(argException.getMessage(), message);
    }
}
