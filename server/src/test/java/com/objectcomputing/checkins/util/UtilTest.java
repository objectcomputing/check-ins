package com.objectcomputing.checkins.util;

import com.objectcomputing.checkins.services.TestContainersSuite;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilTest extends TestContainersSuite {

    private static final String sampleUUIDString = "c0df7236-f200-11ea-adc1-0242ac120002";
    private static final UUID sampleUUID = UUID.fromString(sampleUUIDString);

    @Test
    void testNullSafeUUIDTranslationNotNull() {
        String result = Util.nullSafeUUIDToString(sampleUUID);
        assertEquals(sampleUUIDString, result);
    }

    @Test
    void testNullSafeUUIDTranslationNull() {
        String result = Util.nullSafeUUIDToString(null);
        assertNull(result);
    }
}
