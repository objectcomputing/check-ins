package com.objectcomputing.checkins.services.skills;

import com.objectcomputing.checkins.services.skills.tags.SkillTag;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
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

    @Column(name = "description")
    @Schema(description = "the description of the skill")
    private String description;

    @NotNull
    @Column(name = "extraneous")
    @Schema(description = "the skill is extraneous (or not)", required = true)
    private boolean extraneous = false;

    @ManyToMany(mappedBy = "skills",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<SkillTag> tags;

    public Skill() {
    }

    public Skill(String name) {
        this(name, true, null, false);
    }

    public Skill(String name, boolean pending, String description, boolean extraneous) {
        this.name = name;
        this.pending = pending;
        this.description = description;
        this.extraneous = extraneous;
    }

    public Skill(UUID id, String name, boolean pending, String description, boolean extraneous) {
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

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isExtraneous() {
        return extraneous;
    }

    public void setExtraneous(boolean extraneous) {
        this.extraneous = extraneous;
    }

    public List<SkillTag> getTags() {
        return tags;
    }

    public void setTags(List<SkillTag> tags) {
        this.tags = tags;
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
