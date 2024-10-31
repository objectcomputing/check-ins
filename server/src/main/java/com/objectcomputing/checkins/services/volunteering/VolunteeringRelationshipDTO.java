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
public class VolunteeringRelationshipDTO {

    @NotNull
    @Schema(description = "id of the member with the relationship")
    private UUID memberId;

    @NotNull
    @Schema(description = "id of the organization with the relationship")
    private UUID organizationId;

    @Nullable
    @Schema(description = "when the relationship started")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Nullable
    @Schema(description = "(optionally) when the relationship ended")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Nullable
    @Schema(description = "whether the Volunteering Relationship is active")
    private Boolean active;

    public VolunteeringRelationshipDTO(@NotNull UUID memberId, @NotNull UUID organizationId, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        this(memberId, organizationId, startDate, endDate, true);
    }
}