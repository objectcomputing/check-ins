package com.objectcomputing.checkins.services.demographics;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "demographics")
public class Demographics {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the id of the demographics")
    private UUID id;

    @NotNull
    @Column(name="memberid")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "the userId of the employee")
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
