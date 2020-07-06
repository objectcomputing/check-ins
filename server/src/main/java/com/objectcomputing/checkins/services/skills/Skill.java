package com.objectcomputing.checkins.services.skills;

import io.micronaut.data.annotation.AutoPopulated;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name ="skills")
public class Skill {

    public Skill() {
    }

    public Skill(String name) {
        this(name, true);
    }

    public Skill(String name, boolean pending) {
        this.name = name;
        this.pending = pending;
    }

    @Id
    @Column(name="skillid")
    @AutoPopulated
    private UUID skillid;

    @NotBlank
    @NotNull
    @Column(name="name", nullable = true)
    private String name;

    @Column(name="pending")
    private boolean pending = true;

    public UUID getSkillid() {
        return skillid;
    }

    public void setSkillid(UUID skillid) {
        this.skillid = skillid;
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
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                ", pending=" + pending +
                '}';
    }
}
