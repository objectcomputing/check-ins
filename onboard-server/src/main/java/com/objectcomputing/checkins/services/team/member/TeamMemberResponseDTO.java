package com.objectcomputing.checkins.services.team.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Introspected
public class TeamMemberResponseDTO {

    @Schema(description = "id of the entry", required = true)
    private UUID id;

    @Schema(description = "first name of the member this entry is associated with", required = true)
    private String firstName;

    @Schema(description = "last name of the member this entry is associated with", required = true)
    private String lastName;

    @Schema(description = "full name of the member this entry is associated with", required = true)
    private String name;

    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    private UUID teamId;
    private UUID memberId;

    public TeamMemberResponseDTO(UUID id, String firstName, String lastName, UUID memberId, Boolean lead) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = firstName + ' ' + lastName;
        this.memberId = memberId;
        this.lead = lead;
    }

    public TeamMemberResponseDTO(UUID teamId, UUID memberId, Boolean lead) {
        this.teamId = teamId;
        this.memberId = memberId;
        this.lead = lead;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
