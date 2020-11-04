package com.objectcomputing.checkins.services.team.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class TeamMemberDTO {

    @NotNull
    @Schema(description = "id of the entry", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "name of the member this entry is associated with", required = true)
    private String name;

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public TeamMemberDTO(String id, String name, Boolean lead) {
        if (id != null) {
            this.id = UUID.fromString(id);
        }
        this.name = name;
        this.lead = lead;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isLead() {
        return lead;
    }

    public void setLead(Boolean lead) {
        this.lead = lead;
    }
}
