package com.objectcomputing.checkins.services.team;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @Column(name = "teamid")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the team", required = true)
    private UUID teamid;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "name of the team")
    private String name;

    @NotBlank
    @Column(name = "description")
    @Schema(description = "description of the team")
    private String description;

    public Team(String name, String description) {
        this(null, name, description);
    }

    public Team(UUID teamid, String name, String description) {
        this.teamid = teamid;
        this.name = name;
        this.description = description;
    }

    public UUID getTeamid() {
        return teamid;
    }

    public void setTeamid(UUID teamid) {
        this.teamid = teamid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(teamid, team.teamid) &&
                Objects.equals(name, team.name) &&
                Objects.equals(description, team.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamid, name, description);
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamid=" + teamid +
                ", name='" + name + '\'' +
                ", description='" + description +
                '}';
    }
}
