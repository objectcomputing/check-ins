package com.objectcomputing.checkins.security.authentication.token.util;

import java.util.Map;

import static com.objectcomputing.checkins.security.authentication.token.util.TypeUtilities.*;

public class InterpretableObjectMap {
    private final Map<String, Object> dataset;

    public InterpretableObjectMap(Map<String, Object> dataset) {
        this.dataset = dataset;
    }

    private Object get(String key) {
        return dataset.get(key);
    }

    public String getAsString(String key) {
        Object value = get(key);
        return asString(value);
    }

    public Integer getAsInteger(String key) {
        Object value = get(key);
        return asInteger(value);
    }

    public Long getAsLong(String key) {
        Object value = get(key);
        return asLong(value);
    }

    public Boolean getAsBoolean(String key) {
        Object value = get(key);
        return asBoolean(value);
    }

    public String[] getAsStringArray(String key) {
        Object value = get(key);
        return asStringArray(value);
    }
}
