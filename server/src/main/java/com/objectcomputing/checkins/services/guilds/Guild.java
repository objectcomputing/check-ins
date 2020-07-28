package com.objectcomputing.checkins.services.guilds;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonDeserialize(using = GuildDeserializer.class)
@JsonSerialize(using = GuildSerializer.class)
@Entity
@Table(name = "guilds")
public class Guild {

    public Guild(String name, String description) {
        this(null, name, description, new ArrayList<>());
    }

    public Guild(UUID guildId, String name, String description, ArrayList<GuildMember> members) {
        this.guildId = guildId;
        this.name = name;
        this.description = description;
        this.members = members;
    }

    @Id
    @Column(name="guildId")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    private UUID guildId;

    @NotBlank
    @Column(name="name", unique = true)
    private String name;

    @NotBlank
    @Column(name="description")
    private String description;

    @Transient
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "guildId")
    private List<GuildMember> members;

    public UUID getGuildId() {
        return guildId;
    }

    public void setGuildId(UUID guildId) {
        this.guildId = guildId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Schema(implementation = Map.class)
    public List<GuildMember> getMembers() {
        return members;
    }

    @Hidden
    public List<UUID> getMembersUUIDs() {
        return members != null ? members.stream().map(GuildMember::getMemberId).collect(Collectors.toList()) : null;
    }

    @Hidden
    public List<UUID> getLeadsUUIDS() {
        return members != null ? members.stream().filter(GuildMember::isLead).map(GuildMember::getMemberId).collect(Collectors.toList()) : null;
    }

    @Hidden
    public boolean isLead(UUID memberId) {
        return members != null && members.stream().anyMatch(m -> m.getMemberId().equals(memberId));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Guild{" +
                "guildId=" + guildId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", members=" + members +
                '}';
    }
}
