package com.objectcomputing.checkins.services.member_skill;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "member_skills")
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

    @Column(name="skilllevel")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the member's expertise level for this skill")
    private String skilllevel;

    @Column(name="lastuseddate")
    @TypeDef(type= DataType.DATE)
    @Schema(description = "the last used date of the skill")
    private LocalDate lastuseddate;

    public MemberSkill() {
    }

    public MemberSkill(UUID memberid, UUID skillid) {
        this.memberid = memberid;
        this.skillid = skillid;
    }

    public MemberSkill(UUID id, UUID memberid, UUID skillid) {
        this.id = id;
        this.memberid = memberid;
        this.skillid = skillid;
    }

    public MemberSkill(UUID memberid, UUID skillid, String skilllevel, LocalDate lastuseddate) {
        this.memberid = memberid;
        this.skillid = skillid;
        this.skilllevel = skilllevel;
        this.lastuseddate = lastuseddate;
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

    public String getSkilllevel() {
        return skilllevel;
    }

    public void setSkilllevel(String skilllevel) {
        this.skilllevel = skilllevel;
    }

    public LocalDate getLastuseddate() {
        return lastuseddate;
    }

    public void setLastuseddate(LocalDate lastuseddate) {
        this.lastuseddate = lastuseddate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberSkill that = (MemberSkill) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(memberid, that.memberid) &&
                Objects.equals(skillid, that.skillid) &&
                Objects.equals(skilllevel, that.skilllevel) &&
                Objects.equals(lastuseddate, that.lastuseddate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberid, skillid, skilllevel, lastuseddate);
    }

    @Override
    public String toString() {
        return "MemberSkill {" +
                "id='" + id + '\'' +
                "memberid='" + memberid + '\'' +
                ", skillid=" + skillid + '\'' +
                ", skilllevel=" + skilllevel + '\'' +
                ", lastuseddate=" + lastuseddate +
                '}';
    }
}
