package com.objectcomputing.checkins.services.guild;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class GuildCreateDTO {
    @NotBlank
    @Schema(required = true, description = "name of the guild")
    private String name;

    @Nullable
    @Schema(description = "description of the guild")
    private String description;

    @Schema(description = "members of this guild")
    private List<GuildMemberCreateDTO> guildMembers;

    @Nullable
    @Schema(description="link to the homepage of the guild")
    private String link;
    

    public GuildCreateDTO(String name, @Nullable String description, @Nullable String link) {
        this.name = name;
        this.description = description;
        this.link =link;
    }

    public GuildCreateDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.objectcomputing.checkins.services.guild.GuildCreateDTO that = (com.objectcomputing.checkins.services.guild.GuildCreateDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) && Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    public List<GuildMemberCreateDTO> getGuildMembers() {
        return guildMembers;
    }

    public void setGuildMembers(List<GuildMemberCreateDTO> guildMembers) {
        this.guildMembers = guildMembers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Introspected
    public static class GuildMemberCreateDTO {

        @Schema(description = "whether member is lead or not represented by true or false respectively",
                nullable = true)
        private Boolean lead;

        @NotNull
        @Schema(description = "Member who is on this guild")
        private UUID memberId;

        public GuildMemberCreateDTO(UUID memberId, Boolean lead) {
            this.memberId = memberId;
            this.lead = lead;
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

        public void setMemberId(UUID memberId) {
            this.memberId = memberId;
        }
    }
}
