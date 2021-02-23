package com.objectcomputing.checkins.services.guild;

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
@Table(name = "guilds")
public class Guild {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the guild", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "name of the guild")
    private String name;

    @NotBlank
    @Column(name = "description")
    @Schema(description = "description of the guild")
    private String description;

    public Guild(String name, String description) {
        this(null, name, description);
    }

    public Guild(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guild guild = (Guild) o;
        return Objects.equals(id, guild.id) &&
                Objects.equals(name, guild.name) &&
                Objects.equals(description, guild.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description);
    }

    @Override
    public String toString() {
        return "Guild{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description +
                '}';
    }
}
