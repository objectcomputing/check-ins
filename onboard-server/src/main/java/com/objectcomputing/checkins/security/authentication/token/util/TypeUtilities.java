package com.objectcomputing.checkins.security.authentication.token.util;

import com.objectcomputing.checkins.security.authentication.token.text.DateTimeUtils;

import java.text.ParseException;
import java.util.*;

public class TypeUtilities {
    public static <T> Optional<T> asType(Object value, Class<T> type) {
        if(value == null) {
            return Optional.empty();
        }

        if (!type.isInstance(value)) {
            if(type.equals(Long.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).longValue();
                } else {
                    value = Long.valueOf(value.toString());
                }
            } else if(type.equals(Integer.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).intValue();
                } else {
                    value = Integer.valueOf(value.toString());
                }
            } else if(type.equals(Double.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).doubleValue();
                } else {
                    value = Double.valueOf(value.toString());
                }
            } else if(type.equals(Boolean.class)) {
                value = Boolean.valueOf(value.toString());
            } else if(type.equals(Date.class)) {
                try {
                    value = DateTimeUtils.parseDate(value.toString());
                } catch (ParseException e) {
                    throw new RuntimeException("value type mismatch (date)");
                }
            } else if(type.equals(String.class)) {
                value = value.toString();
            } else {
                throw new RuntimeException("unknown type");
            }
        }
        return Optional.ofNullable((T)value);
    }

    public static Boolean asBoolean(Object value) {
        if(null != value && !isBoolean(value.getClass())) {
            if(value.getClass() != String.class) {
                value = value.toString();
            }
            value = Boolean.valueOf((String) value);
        }
        return (Boolean) value;
    }

    private static boolean isBoolean(Class<?> type) {
        return type == Boolean.class || type == boolean.class;
    }

    public static String asString(Object value) {
        if(null != value && value.getClass() != String.class) {
            value = value.toString();
        }
        return (String)value;
    }

    public static String[] asStringArray(Object value) {
        if(value instanceof Collection) {
            value = List.copyOf((Collection) value).stream().map(TypeUtilities::asString).toArray();
        } else if(value.getClass().isArray()) {
            value = Arrays.stream((Object[]) value).map(TypeUtilities::asString).toArray();
        }
        return (String[]) value;
    }

    public static Integer asInteger(Object value) {
        if(null != value && !isInteger(value.getClass())) {
            if(value instanceof Number) {
                value = ((Number) value).intValue();
            } else {
                value = Integer.valueOf(value.toString());
            }
        }
        return (Integer) value;
    }

    private static boolean isInteger(Class<?> type) {
        return type == Integer.class || type == int.class;
    }

    public static Long asLong(Object value) {
        if(null != value && !isLong(value.getClass())) {
            if(value instanceof Number) {
                value = ((Number) value).longValue();
            } else {
                value = Long.valueOf(value.toString());
            }
        }
        return (Long) value;
    }

    private static boolean isLong(Class<?> type) {
        return type == Long.class || type == long.class;
    }

}
