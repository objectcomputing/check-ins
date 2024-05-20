package com.objectcomputing.checkins.services.checkins;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class CheckInCreateDTO {
    
    @NotNull
    @Schema(description = "id of team member")
    private UUID teamMemberId;

    @NotNull
    @Schema(description = "id of pdl")
    private UUID pdlId;

    @Schema(description = "date of checkin")
    private LocalDateTime checkInDate;

    @NotNull
    @Schema(description = "whether checkin is completed or not")
    private boolean completed;
}