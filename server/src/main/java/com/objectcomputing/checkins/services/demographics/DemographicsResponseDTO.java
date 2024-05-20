package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class DemographicsResponseDTO {

    @NotNull
    @Schema(description = "the id of the demographics")
    private UUID id;

    @NotNull
    @Schema(description = "the userId of the employee")
    private UUID memberId;

    @Nullable
    @Schema(description = "the gender of the employee")
    private String gender;

    @Nullable
    @Schema(description = "the degree level of the employee")
    private String degreeLevel;

    @Nullable
    @Schema(description = "the industry tenure of the employee")
    private Integer industryTenure;

    @Nullable
    @Schema(description = "whether the employee is a person of color")
    private Boolean personOfColor = false;

    @Nullable
    @Schema(description = "whether the employee is a veteran")
    private Boolean veteran = false;

    @Nullable
    @Schema(description = "the military tenure of the employee")
    private Integer militaryTenure;

    @Nullable
    @Schema(description = "the military branch of the employee")
    private String militaryBranch;
}
