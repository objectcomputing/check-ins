package com.objectcomputing.checkins.services.skills.tags;

import com.objectcomputing.checkins.services.skills.Skill;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
@Entity
@Table(name = "skill_tags")
public class SkillTag {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the skill tag", required = true)
    private UUID id;

    @Column(name = "name")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the name of the skill tag", required = true)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinTable(name = "skills_skill_tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    public SkillTag() {}

    public SkillTag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SkillTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillTag skillTag = (SkillTag) o;
        return Objects.equals(id, skillTag.id) &&
                Objects.equals(name, skillTag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
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
}
