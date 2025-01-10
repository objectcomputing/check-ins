package com.objectcomputing.checkins.services.volunteering;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@Introspected
public class VolunteeringEventDTO {

    @NotNull
    @Schema(description = "id of the Volunteering relationship")
    private UUID relationshipId;

    @NotNull
    @Schema(description = "when the volunteering event occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate eventDate;

    @NotNull
    @Schema(description = "number of hours spent volunteering")
    private Double hours;

    @Nullable
    @Schema(description = "notes about the volunteering event")
    private String notes;
}
