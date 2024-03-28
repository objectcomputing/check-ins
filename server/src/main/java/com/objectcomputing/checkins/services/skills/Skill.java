package com.objectcomputing.checkins.services.skills;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "skills")
public class Skill {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "The id of the skill", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "The name of the skill", required = true)
    private String name;

    @NotNull
    @Column(name = "pending")
    @Schema(description = "The pending status (approved or not) of the skill")
    private Boolean pending = true;

    @Column(name = "description")
    @Schema(description = "The description of the skill")
    private String description;

    @NotNull
    @Column(name = "extraneous")
    @Schema(description = "The skill is extraneous (or not)", required = true)
    private Boolean extraneous = false;

    public Skill() {
    }

    public Skill(String name) {
        this(name, true, null, false);
    }

    public Skill(String name, String description) {
        this(name, true, description, false);
    }

    public Skill(String name, Boolean pending, String description, Boolean extraneous) {
        this.name = name;
        this.pending = pending;
        this.description = description;
        this.extraneous = extraneous;
    }

    public Skill(UUID id, String name, Boolean pending, String description, Boolean extraneous) {
        this.id = id;
        this.name = name;
        this.pending = pending;
        this.description = description;
        this.extraneous = extraneous;
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

    public Boolean isPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isExtraneous() {
        return extraneous;
    }

    @SuppressWarnings("unused")
    public void setExtraneous(Boolean extraneous) {
        this.extraneous = extraneous;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return pending == skill.pending &&
                Objects.equals(id, skill.id) &&
                Objects.equals(name, skill.name) &&
                Objects.equals(description, skill.description) &&
                extraneous == skill.extraneous;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pending, description, extraneous);
    }

    @Override
    public String toString() {
        return "Skill {" +
                "name='" + name + '\'' +
                ", pending=" + pending + '\'' +
                ", description=" + description + '\'' +
                ", extraneous=" + extraneous + '\'' +
                '}';
    }
}
