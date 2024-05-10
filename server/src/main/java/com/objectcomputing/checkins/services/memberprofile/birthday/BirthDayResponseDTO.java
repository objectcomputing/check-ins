package com.objectcomputing.checkins.services.memberprofile.birthday;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class BirthDayResponseDTO {
    @NotBlank
    @Schema(description = "Name of the employee")
    private String name;

    @NotBlank
    @Schema(description = "Birth date of the employee")
    private String birthDay;

    @NotNull
    @Schema(description = "Id of the member profile this employee is associated with")
    private UUID userId;

}
