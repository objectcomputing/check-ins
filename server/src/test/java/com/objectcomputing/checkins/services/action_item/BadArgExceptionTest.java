package com.objectcomputing.checkins.services.action_item;

import com.objectcomputing.checkins.services.exceptions.BadArgException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        BadArgException argException = new BadArgException(message);
        assertEquals(argException.getMessage(), message);
    }

}
