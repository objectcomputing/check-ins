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
    private UUID memberIdId;

    @Column(name="gender")
    @Schema(description = "the gender of the employee")
    private String gender;

    @Column(name="degreeLevel")
    @Schema(description = "the degree level of the employee")
    private String degreeLevel;

    @Column(name="industryTenure")
    @Schema(description = "the industry tenure of the employee")
    private int industryTenure;

    @Column(name="personOfColor")
    @Schema(description = "whether the employee is a person of color")
    private boolean personOfColor;

    @Column(name="veteran")
    @Schema(description = "whether the employee is a veteran")
    private boolean veteran;

    @Column(name="militaryTenure")
    @Schema(description = "the military tenure of the employee")
    private int militaryTenure;

    @Column(name="militaryBranch")
    @Schema(description = "the military branch of the employee")
    private int militaryBranch;

    public Demographics(UUID id,
                        @NotNull UUID memberIdId,
                        String gender,
                        String degreeLevel,
                        int industryTenure,
                        boolean personOfColor,
                        boolean veteran,
                        int militaryTenure,
                        int militaryBranch) {
        this.id = id;
        this.memberIdId = memberIdId;
        this.gender = gender;
        this.degreeLevel = degreeLevel;
        this.industryTenure = industryTenure;
        this.personOfColor = personOfColor;
        this.veteran = veteran;
        this.militaryTenure = militaryTenure;
        this.militaryBranch = militaryBranch;
    }



}
