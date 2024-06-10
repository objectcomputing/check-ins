package com.objectcomputing.checkins.services.volunteering;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.objectcomputing.checkins.converter.LocalDateConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Introspected
public class VolunteeringRelationshipDTO {

    @NotNull
    @Schema(description = "id of the member with the relationship")
    private UUID memberId;

    @NotNull
    @Schema(description = "id of the organization with the relationship")
    private UUID organizationId;

    @NotNull
    @Schema(description = "when the relationship started")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Nullable
    @Schema(description = "(optionally) when the relationship ended")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "whether the Volunteering Relationship is active")
    private Boolean active = true;
}