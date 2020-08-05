package com.objectcomputing.checkins.services.agenda;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class AgendaItemCreateDTO {
    @NotNull
    @Schema(description = "id of the checkin this entry is associated with", required = true)
    private UUID checkinid;

    @NotNull
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID createdbyid;

    @Schema(description = "description of the agenda item",
            nullable = true)
    private String description;

    public UUID getCheckinid() {
        return checkinid;
    }

    public void setCheckinid(UUID checkinid) {
        this.checkinid = checkinid;
    }

    public UUID getCreatedbyid() {
        return createdbyid;
    }

    public void setCreatedbyid(UUID createdbyid) {
        this.createdbyid = createdbyid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

// package com.objectcomputing.checkins.services.agenda;

// public class AgendaCreateDTO {
    
// }