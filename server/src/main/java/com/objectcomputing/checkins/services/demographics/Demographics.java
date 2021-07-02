package com.objectcomputing.checkins.services.demographics;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name="demographics")
public class Demographics {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the demographics", required = true)
    private UUID id;

    @NotNull
    @Column(name="memberId")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the userId of the employee", required = true)
    private UUID memberId;

    @Column(name="gender")
    @Schema(description = "the gender of the employee")
    private String gender;

    @Column(name="degreeLevel")
    @Schema(description = "the degree level of the employee")
    private String degreeLevel;

    @Column(name="industryTenure")
    @Schema(description = "the industry tenure of the employee")
    private Integer industryTenure;

    @Column(name="personOfColor")
    @Schema(description = "whether the employee is a person of color")
    private boolean personOfColor = false;

    @Column(name="veteran")
    @Schema(description = "whether the employee is a veteran")
    private boolean veteran = false;

    @Column(name="militaryTenure")
    @Schema(description = "the military tenure of the employee")
    private Integer militaryTenure;

    @Column(name="militaryBranch")
    @Schema(description = "the military branch of the employee")
    private String militaryBranch;

    public Demographics(UUID id,
                        @NotNull UUID memberId,
                        String gender,
                        String degreeLevel,
                        Integer industryTenure,
                        boolean personOfColor,
                        boolean veteran,
                        Integer militaryTenure,
                        String militaryBranch) {
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
                        String gender,
                        String degreeLevel,
                        int industryTenure,
                        boolean personOfColor,
                        boolean veteran,
                        int militaryTenure,
                        String militaryBranch) {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDegreeLevel() {
        return degreeLevel;
    }

    public void setDegreeLevel(String degreeLevel) {
        this.degreeLevel = degreeLevel;
    }

    public Integer getIndustryTenure() {
        return industryTenure;
    }

    public void setIndustryTenure(Integer industryTenure) {
        this.industryTenure = industryTenure;
    }

    public boolean isPersonOfColor() {
        return personOfColor;
    }

    public void setPersonOfColor(boolean personOfColor) {
        this.personOfColor = personOfColor;
    }

    public boolean isVeteran() {
        return veteran;
    }

    public void setVeteran(boolean veteran) {
        this.veteran = veteran;
    }

    public Integer getMilitaryTenure() {
        return militaryTenure;
    }

    public void setMilitaryTenure(Integer militaryTenure) {
        this.militaryTenure = militaryTenure;
    }

    public String getMilitaryBranch() {
        return militaryBranch;
    }

    public void setMilitaryBranch(String militaryBranch) {
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
