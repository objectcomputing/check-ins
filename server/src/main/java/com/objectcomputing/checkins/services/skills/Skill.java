package com.objectcomputing.checkins.services.skills;

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
@Table(name = "skills")
public class Skill {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the skill", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "name", unique = true)
    @Schema(description = "the name of the skill", required = true)
    private String name;

    @Column(name = "pending")
    @Schema(description = "the pending status (approved or not) of the skill")
    private boolean pending = true;

    public Skill() {
    }

    public Skill(String name) {
        this(name, true);
    }

    public Skill(String name, boolean pending) {
        this.name = name;
        this.pending = pending;
    }

    public Skill(UUID id, String name, boolean pending) {
        this.name = name;
        this.pending = pending;
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

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return pending == skill.pending &&
                Objects.equals(id, skill.id) &&
                Objects.equals(name, skill.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, pending);
    }

    @Override
    public String toString() {
        return "Skill {" +
                "name='" + name + '\'' +
                ", pending=" + pending +
                '}';
    }
}
