package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class DemographicsUpdateDTO {

    @NotNull
    @Schema(description = "the id of the demographics", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "the userId of the employee", required = true)
    private UUID memberId;

    @Nullable
    @Schema(description = "the gender of the employee")
    private String gender;

    @Nullable
    @Schema(description = "the degree level of the employee")
    private String degreeLevel;

    @Nullable
    @Schema(description = "the industry tenure of the employee")
    private Integer industryTenure;

    @Nullable
    @Schema(description = "whether the employee is a person of color")
    private Boolean personOfColor = false;

    @Nullable
    @Schema(description = "whether the employee is a veteran")
    private Boolean veteran = false;

    @Nullable
    @Schema(description = "the military tenure of the employee")
    private Integer militaryTenure;

    @Nullable
    @Schema(description = "the military branch of the employee")
    private String militaryBranch;

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
}
