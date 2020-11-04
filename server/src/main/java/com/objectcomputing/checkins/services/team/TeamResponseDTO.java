package com.objectcomputing.checkins.services.team;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class TeamResponseDTO {
    @NotNull
    private UUID id;

    @NotBlank
    @Schema(required = true, description = "name of the team")
    private String name;

    @NotBlank
    @Schema(required = true, description = "description of the team")
    private String description;

    public TeamResponseDTO(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public TeamResponseDTO(String id, String name, String description) {
        this(UUID.fromString(id), name, description);
    }

    public TeamResponseDTO() {}

    @Override
    public String toString() {
        return "TeamResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamResponseDTO that = (TeamResponseDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
