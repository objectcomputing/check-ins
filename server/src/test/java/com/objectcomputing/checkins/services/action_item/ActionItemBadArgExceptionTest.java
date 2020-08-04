package com.objectcomputing.checkins.services.action_item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActionItemBadArgExceptionTest {

    @Test
    void testExceptionMessage() {
        final String message = "Hello world";
        ActionItemBadArgException argException = new ActionItemBadArgException(message);
        assertEquals(argException.getMessage(), message);
    }

}
