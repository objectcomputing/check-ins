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
    @Column(name="uuid")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the member profile this entry is associated with", required = true)
    private UUID uuid;

    @NotNull
    @Column(name = "name", nullable = false)
    @Schema(description = "full name of the employee", required = true)
    private String name;

    @Column(name="role")
    @Schema(description = "employee's role at the company", required = true)
    private String role ;
    
    @Column(name="pdlId")
    @TypeDef(type=DataType.STRING)
    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @Column(name="location")
    @Schema(description = "where the employee is geographically located", required = true)
    private String location;

    @NotNull
    @Column(name="workEmail")
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjctComputing.com", required = true)
    private String workEmail;

    @Column(name="insperityId")
    @Schema(description = "unique identifier for this employee with the Insperity system")
    private String insperityId; 

    @Column(name="startDate")
    @Schema(description = "employee's date of hire", required = true)
    @Nullable
    private LocalDate startDate;

    @Column(name="bioText")
    @Schema(description = "employee's biography")
    private String bioText;

    public MemberProfile(String name, String role, @Nullable UUID pdlId, String location,
                         String workEmail, String insperityId, @Nullable LocalDate startDate,
                         String bioText) {
       this(null, name, role, pdlId, location, workEmail, insperityId, startDate, bioText);
    }

    public MemberProfile(UUID id, String name, String role, @Nullable UUID pdlId, String location,
                         String workEmail, String insperityId, @Nullable LocalDate startDate,
                         String bioText) {
        this.uuid = id;
        this.name=name;
        this.role=role;
        this.pdlId=pdlId;
        this.location=location;
        this.workEmail=workEmail;
        this.insperityId=insperityId;
        this.startDate=startDate;
        this.bioText=bioText;
    }

    public MemberProfile() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(name, that.name) &&
                Objects.equals(role, that.role) &&
                Objects.equals(pdlId, that.pdlId) &&
                Objects.equals(location, that.location) &&
                Objects.equals(workEmail, that.workEmail) &&
                Objects.equals(insperityId, that.insperityId) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(bioText, that.bioText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, role, pdlId, location, workEmail, insperityId, startDate, bioText);
    }

    @Override
    public String toString() {
        return "MemberProfile{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", pdlId=" + pdlId +
                ", location='" + location + '\'' +
                ", workEmail='" + workEmail + '\'' +
                ", insperityId='" + insperityId + '\'' +
                ", startDate=" + startDate +
                ", bioText='" + bioText + '\'' +
                '}';
    }
}
