package com.objectcomputing.checkins.services.action_item;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class ActionItemUpdateDTO {

    @NotNull
    @Schema(description = "id of the action item to update", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "id of the checkin this entry is associated with", required = true)
    private UUID checkinid;

    @NotNull
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID createdbyid;

    @Schema(description = "description of the action item",
            nullable = true)
    private String description;

    @NotNull
    @Schema(description = "ordering priority for this action item on the checkin", required = true)
    private Double priority;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Double getPriority() {
        return priority;
    }

    public void setPriority(Double priority) {
        this.priority = priority;
    }
}
