package com.objectcomputing.checkins.services.agenda_item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AgendaItemBadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        AgendaItemBadArgException argException = new AgendaItemBadArgException(message);
        assertEquals(argException.getMessage(), message);
    }

}
