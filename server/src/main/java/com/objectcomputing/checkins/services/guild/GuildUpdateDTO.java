package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import com.objectcomputing.checkins.services.guild.member.GuildMemberUpdateDTO;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDFromString;

@Introspected
public class GuildUpdateDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the guild")
    private String name;

    @Nullable
    @Schema(description = "description of the guild")
    private String description;

    @Nullable
    @Schema(description="link to the homepage of the guild")
    private String link;

    @Schema(description = "members of this guild")
    private List<GuildMemberUpdateDTO> guildMembers;


    public GuildUpdateDTO(UUID id, String name, @Nullable String description, @Nullable String link) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
    }

    public GuildUpdateDTO(String id, String name, String description, @Nullable String link) {
        this(nullSafeUUIDFromString(id), name, description, link);
    }

    public GuildUpdateDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "GuildUpdateDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link+ '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.objectcomputing.checkins.services.guild.GuildUpdateDTO updateDTO = (com.objectcomputing.checkins.services.guild.GuildUpdateDTO) o;
        return Objects.equals(id, updateDTO.id) &&
                Objects.equals(name, updateDTO.name) &&
                Objects.equals(description, updateDTO.description) &&
                Objects.equals(link, updateDTO.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description,link);
    }

    public List<GuildMemberUpdateDTO> getGuildMembers() {
        return guildMembers;
    }

    public void setGuildMembers(List<GuildMemberUpdateDTO> guildMembers) {
        this.guildMembers = guildMembers;
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

    @Nullable
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Introspected
    public static class GuildMemberUpdateDTO {

        @Schema(description = "ID of the entity to update")
        private UUID id;

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this guild")
        private UUID memberId;

        public GuildMemberUpdateDTO(UUID id, UUID memberId, Boolean lead) {
            this.id = id;
            this.memberId = memberId;
            this.lead = lead;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public Boolean getLead() {
            return lead;
        }

        public void setLead(Boolean lead) {
            this.lead = lead;
        }

        public UUID getMemberId() {
            return memberId;
        }

        public void setMemberId(UUID memberid) {
            this.memberId = memberid;
        }
    }
}