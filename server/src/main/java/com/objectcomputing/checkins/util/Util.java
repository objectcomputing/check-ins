package com.objectcomputing.checkins.util;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Util {

    public static final LocalDateTime MIN = LocalDateTime.parse("1990-01-01T01:01:01");
    public static final LocalDateTime MAX = LocalDateTime.parse("2099-01-01T01:01:01");

    public static String nullSafeUUIDToString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    public static UUID nullSafeUUIDFromString(String id) {
        return id == null ? null : UUID.fromString(id);
    }

    public static List<String> nullSafeUUIDListToStringList(List<UUID> uuidList) {
        LinkedList<String> stringsList = null;
        if(uuidList != null) {
            stringsList = new LinkedList<>();
            for(UUID uuid: uuidList) {
                String uuidString = nullSafeUUIDToString(uuid);
                if(uuidString != null) {
                    stringsList.add(uuidString);
                }
            }
        }
        return stringsList;
    }
}
