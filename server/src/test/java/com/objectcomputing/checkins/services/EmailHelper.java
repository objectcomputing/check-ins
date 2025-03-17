package com.objectcomputing.checkins.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class EmailHelper {
    public static void validateEmail(String action, String fromName,
                                     String fromAddress, String subject,
                                     String partialBody, String recipients,
                                     List<String> event) {
        assertEquals(6, event.size());
        assertEquals(action, event.get(0));
        assertEquals(fromName, event.get(1));
        assertEquals(fromAddress, event.get(2));
        assertEquals(subject, event.get(3));
        if (partialBody != null && !partialBody.isEmpty()) {
            assertTrue(event.get(4).contains(partialBody));
        }
        if (recipients != null && !recipients.isEmpty()) {
            assertEquals(recipients, event.get(5));
        }
    }
}
