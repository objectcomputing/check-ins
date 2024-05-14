package com.objectcomputing.checkins.services.memberprofile.retentionreport;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Introspected
public class Interval {

    private LocalDate date;

    @NotNull
    private Float value;

    public Interval(LocalDate date, Float value) {
        this.date = date;
        this.value = value;
    }

}
