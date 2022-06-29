package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Nullable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "demographics")
public class Demographics {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the demographics", required = true)
    private UUID id;

    @NotNull
    @Column(name="memberid")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the userId of the employee", required = true)
    private UUID memberId;

    @Nullable
    @Column(name="gender")
    @Schema(description = "the gender of the employee")
    private String gender;

    @Nullable
    @Column(name="degreelevel")
    @Schema(description = "the degree level of the employee")
    private String degreeLevel;

    @Nullable
    @Column(name="industrytenure")
    @Schema(description = "the industry tenure of the employee")
    private Integer industryTenure;

    @Nullable
    @Column(name="personofcolor")
    @Schema(description = "whether the employee is a person of color")
    private Boolean personOfColor = false;

    @Nullable
    @Column(name="veteran")
    @Schema(description = "whether the employee is a veteran")
    private Boolean veteran = false;

    @Nullable
    @Column(name="militarytenure")
    @Schema(description = "the military tenure of the employee")
    private Integer militaryTenure;

    @Nullable
    @Column(name="militarybranch")
    @Schema(description = "the military branch of the employee")
    private String militaryBranch;

    public Demographics() {
    }

    public Demographics(@Nullable UUID id,
                        @NotNull UUID memberId,
                        @Nullable String gender,
                        @Nullable String degreeLevel,
                        @Nullable Integer industryTenure,
                        @Nullable Boolean personOfColor,
                        @Nullable Boolean veteran,
                        @Nullable Integer militaryTenure,
                        @Nullable String militaryBranch) {
        this.id = id;
        this.memberId = memberId;
        this.gender = gender;
        this.degreeLevel = degreeLevel;
        this.industryTenure = industryTenure;
        this.personOfColor = personOfColor;
        this.veteran = veteran;
        this.militaryTenure = militaryTenure;
        this.militaryBranch = militaryBranch;
    }

    public Demographics(@NotNull UUID memberId,
                        @Nullable String gender,
                        @Nullable String degreeLevel,
                        @Nullable Integer industryTenure,
                        @Nullable Boolean personOfColor,
                        @Nullable Boolean veteran,
                        @Nullable Integer militaryTenure,
                        @Nullable String militaryBranch) {
        this.memberId = memberId;
        this.gender = gender;
        this.degreeLevel = degreeLevel;
        this.industryTenure = industryTenure;
        this.personOfColor = personOfColor;
        this.veteran = veteran;
        this.militaryTenure = militaryTenure;
        this.militaryBranch = militaryBranch;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    @Nullable
    public String getGender() {
        return gender;
    }

    public void setGender(@Nullable String gender) {
        this.gender = gender;
    }

    @Nullable
    public String getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(@Nullable String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    @Nullable
    public Integer getIndustryTenure() {
        return industryTenure;
    }

    public void setIndustryTenure(@Nullable Integer industryTenure) {
        this.industryTenure = industryTenure;
    }

    @Nullable
    public Boolean getPersonOfColor() {
        return personOfColor;
    }

    public void setPersonOfColor(@Nullable Boolean personOfColor) {
        this.personOfColor = personOfColor;
    }

    @Nullable
    public Boolean getVeteran() {
        return veteran;
    }

    public void setVeteran(@Nullable Boolean veteran) {
        this.veteran = veteran;
    }

    @Nullable
    public Integer getMilitaryTenure() {
        return militaryTenure;
    }

    public void setMilitaryTenure(@Nullable Integer militaryTenure) {
        this.militaryTenure = militaryTenure;
    }

    @Nullable
    public String getMilitaryBranch() {
        return militaryBranch;
    }

    public void setMilitaryBranch(@Nullable String militaryBranch) {
        this.militaryBranch = militaryBranch;
    }

    @Override
    public String toString() {
        return "Demographics{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", gender='" + gender + '\'' +
                ", degreeLevel='" + degreeLevel + '\'' +
                ", industryTenure=" + industryTenure +
                ", personOfColor=" + personOfColor +
                ", veteran=" + veteran +
                ", militaryTenure=" + militaryTenure +
                ", militaryBranch=" + militaryBranch +
                '}';
    }
}
