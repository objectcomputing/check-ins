package com.objectcomputing.checkins.services.memberSkills;

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
@Table(name ="member_skills")
public class MemberSkill {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the memberskill", required = true)
    private UUID id;

    @NotNull
    @Column(name="memberid")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the member profile", required = true)
    private UUID memberid;

    @NotNull
    @Column(name="skillid")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the skill", required = true)
    private UUID skillid;

    public MemberSkill() {
    }

    public MemberSkill(@NotNull UUID memberid, @NotNull UUID skillid) {
        this.memberid = memberid;
        this.skillid = skillid;
    }

    public MemberSkill(UUID id, @NotNull UUID memberid, @NotNull UUID skillid) {
        this.id = id;
        this.memberid = memberid;
        this.skillid = skillid;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMemberid() {
        return memberid;
    }

    public void setMemberid(UUID memberid) {
        this.memberid = memberid;
    }

    public UUID getSkillid() {
        return skillid;
    }

    public void setSkillid(UUID skillid) {
        this.skillid = skillid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberSkill that = (MemberSkill) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(memberid, that.memberid) &&
                Objects.equals(skillid, that.skillid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberid, skillid);
    }

    @Override
    public String toString() {
        return "MemberSkill {" +
                "id='" + id + '\'' +
                "memberid='" + memberid + '\'' +
                ", skillid=" + skillid +
                '}';
    }
}
