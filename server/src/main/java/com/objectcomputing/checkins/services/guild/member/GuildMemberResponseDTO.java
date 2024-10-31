package com.objectcomputing.checkins.services.guild.member;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class GuildMemberResponseDTO {

    @Schema(description = "id of the entry")
    private UUID id;

    @Schema(description = "first name of the member this entry is associated with")
    private String firstName;

    @Schema(description = "last name of the member this entry is associated with")
    private String lastName;

    @Schema(description = "full name of the member this entry is associated with")
    private String name;

    @Setter
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    private UUID guildId;
    private UUID memberId;

    public GuildMemberResponseDTO(UUID id, String firstName, String lastName, UUID memberId, Boolean lead) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = firstName + ' ' + lastName;
        this.memberId = memberId;
        this.lead = lead;
    }

    public GuildMemberResponseDTO(UUID guildId, UUID memberId, Boolean lead) {
        this.guildId = guildId;
        this.memberId = memberId;
        this.lead = lead;
    }

    public Boolean isLead() {
        return lead;
    }
}