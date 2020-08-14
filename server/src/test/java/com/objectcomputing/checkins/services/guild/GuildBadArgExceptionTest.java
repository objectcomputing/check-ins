package com.objectcomputing.checkins.services.guild;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GuildBadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        GuildBadArgException argException = new GuildBadArgException(message);
        assertEquals(argException.getMessage(), message);
    }

}
