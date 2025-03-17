package com.objectcomputing.checkins.util.serialize;

import io.micronaut.core.annotation.Introspected;

import java.time.format.DateTimeFormatter;

@Introspected
public class LocalDateTimeSerializer extends com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer {

    protected LocalDateTimeSerializer() {
        super();
    }

    public LocalDateTimeSerializer(DateTimeFormatter t) {
        super(t);
    }
}