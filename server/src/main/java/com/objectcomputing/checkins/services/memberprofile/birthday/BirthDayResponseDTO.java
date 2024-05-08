package com.objectcomputing.checkins.services.memberprofile.birthday;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Introspected
public class BirthDayResponseDTO {
    @NotBlank
    @Schema(description = "Name of the employee", required = true)
    private String name;

    @NotBlank
    @Schema(description = "Birth date of the employee", required = true)
    private String birthDay;

    @NotNull
    @Schema(description = "Id of the member profile this employee is associated with", required = true)
    private UUID userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
