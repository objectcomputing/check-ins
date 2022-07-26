package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class SignRequestRetrievalExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        SignRequestRetrievalException argException = new SignRequestRetrievalException(message);
        assertEquals(argException.getMessage(), message);
    }

}
