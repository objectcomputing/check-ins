package com.objectcomputing.checkins.services.file;

import com.objectcomputing.checkins.services.TestContainersSuite;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileRetrievalExceptionTest extends TestContainersSuite {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        FileRetrievalException argException = new FileRetrievalException(message);
        assertEquals(argException.getMessage(), message);
    }
}
