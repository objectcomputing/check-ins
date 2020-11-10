package com.objectcomputing.checkins.services.memberprofile;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.annotation.Nullable;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name ="member_profile")
public class MemberProfile {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the member profile this entry is associated with", required = true)
    private UUID id;

    @Nullable
    @Column(name = "name")
    @Schema(description = "full name of the employee")
    private String name;

    @Column(name="title")
    @Nullable
    @Schema(description = "employee's title at the company")
    private String title ;
    
    @Column(name="pdlId")
    @TypeDef(type=DataType.STRING)
    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @Column(name="location")
    @Nullable
    @Schema(description = "where the employee is geographically located")
    private String location;

    @NotNull
    @Column(name="workEmail")
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjctComputing.com", required = true)
    private String workEmail;

    @Column(name="insperityId")
    @Nullable
    @Schema(description = "unique identifier for this employee with the Insperity system")
    private String insperityId; 

    @Column(name="startDate")
    @Schema(description = "employee's date of hire")
    @Nullable
    private LocalDate startDate;

    @Column(name="bioText")
    @Nullable
    @Schema(description = "employee's biography")
    private String bioText;

    public MemberProfile(@Nullable String name, @Nullable String title, @Nullable UUID pdlId, @Nullable String location,
                         String workEmail, @Nullable String insperityId, @Nullable LocalDate startDate,
                         @Nullable String bioText) {
       this(null, name, title, pdlId, location, workEmail, insperityId, startDate, bioText);
    }

    public MemberProfile(UUID id, @Nullable String name, @Nullable String title, @Nullable UUID pdlId, @Nullable String location,
                         String workEmail, @Nullable String insperityId, @Nullable LocalDate startDate,
                         @Nullable String bioText) {
        this.id = id;
        this.name=name;
        this.title=title;
        this.pdlId=pdlId;
        this.location=location;
        this.workEmail=workEmail;
        this.insperityId=insperityId;
        this.startDate=startDate;
        this.bioText=bioText;
    }

    public MemberProfile() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getPdlId() {
        return pdlId;
    }

    public void setPdlId(UUID pdlId) {
        this.pdlId = pdlId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getInsperityId() {
        return insperityId;
    }

    public void setInsperityId(String insperityId) {
        this.insperityId = insperityId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getBioText() {
        return bioText;
    }

    public void setBioText(String bioText) {
        this.bioText = bioText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberProfile that = (MemberProfile) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(title, that.title) &&
                Objects.equals(pdlId, that.pdlId) &&
                Objects.equals(location, that.location) &&
                Objects.equals(workEmail, that.workEmail) &&
                Objects.equals(insperityId, that.insperityId) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(bioText, that.bioText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, pdlId, location, workEmail, insperityId, startDate, bioText);
    }

    @Override
    public String toString() {
        return "MemberProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", pdlId=" + pdlId +
                ", location='" + location + '\'' +
                ", workEmail='" + workEmail + '\'' +
                ", insperityId='" + insperityId + '\'' +
                ", startDate=" + startDate +
                ", bioText='" + bioText + '\'' +
                '}';
    }
}
