package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Introspected
public class Interval {

    private LocalDate date;

    @NotNull
    private Float value;

    public Interval(LocalDate date, Float value) {
        this.date = date;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
