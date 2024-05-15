package com.objectcomputing.checkins.converter;

import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;
import jakarta.persistence.AttributeConverter;

import java.sql.Date;
import java.time.LocalDate;


@Singleton
@Introspected
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate localDate) {
        return localDate == null ? null : Date.valueOf(localDate);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}