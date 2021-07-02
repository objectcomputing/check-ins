package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class DemographicsCreateDTO {

    @NotNull
    @Schema(description = "the userId of the employee", required = true)
    private UUID memberId;

    @Nullable
    @Schema(description = "the gender of the employee")
    private String gender;

    @Nullable
    @Schema(description = "the degree level of the employee")
    private String degreeLevel;

    @Schema(description = "the industry tenure of the employee")
    private Integer industryTenure;

    @Schema(description = "whether the employee is a person of color")
    private boolean personOfColor = false;

    @Schema(description = "whether the employee is a veteran")
    private boolean veteran = false;

    @Schema(description = "the military tenure of the employee")
    private Integer militaryTenure;

    @Nullable
    @Schema(description = "the military branch of the employee")
    private String militaryBranch;

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

    @Nullable
    public String getMilitaryBranch() {
        return militaryBranch;
    }

    public void setMilitaryBranch(@Nullable String militaryBranch) {
        this.militaryBranch = militaryBranch;
    }
}
