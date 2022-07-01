package com.objectcomputing.checkins.services.file;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class FileRetrievalExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        FileRetrievalException argException = new FileRetrievalException(message);
        assertEquals(argException.getMessage(), message);
    }
}
