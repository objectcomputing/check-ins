package com.objectcomputing.checkins.services.checkin_notes;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Introspected
public class CheckinNoteCreateDTO {
    @NotNull
    @Schema(description = "id of the checkin this entry is associated with")
    private UUID checkinid;

    @NotNull
    @Schema(description = "id of the member this entry is associated with")
    private UUID createdbyid;

    @Nullable
    @Schema(description = "description of the check in note", nullable = true)
    private String description;

}