package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GuildResponseDTO {

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


    List<GuildMemberResponseDTO> guildMembers;

    public GuildResponseDTO(UUID id, String name, @Nullable String description, @Nullable String link) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
    }

    public GuildResponseDTO(String id, String name, @Nullable String description, @Nullable String link) {
        this(UUID.fromString(id), name, description, link);
    }

    public GuildResponseDTO() {
        id = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "GuildResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link  +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildResponseDTO that = (GuildResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(link, that.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description,link );
    }

    public List<GuildMemberResponseDTO> getGuildMembers() {
        if (guildMembers == null) {
            guildMembers = new ArrayList<>();
        }
        return guildMembers;
    }

    public void setGuildMembers(List<GuildMemberResponseDTO> guildMembers) {
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
}
