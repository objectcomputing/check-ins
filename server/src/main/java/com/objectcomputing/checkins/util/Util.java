package com.objectcomputing.checkins.util;

import java.util.UUID;

public class Util {
    public static String nullSafeUUIDToString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }
}
