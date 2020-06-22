package com.objectcomputing.checkins.services.memberprofile;

import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

import io.micronaut.data.annotation.AutoPopulated;

@Entity
@Table(name ="member_profile")
public class MemberProfile {

    public MemberProfile(String name, String role, UUID pdlId, String location,
                        String workEmail, String insperityId, Date startDate,
                        String bioText) {
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

    @Id
    @Column(name="uuid")
    @AutoPopulated
    private UUID uuid;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name="role")
    private String role ;
    
    @Type(type = "uuid-char")
    @Column(name="pdlId")
    private UUID pdlId;

    @Column(name="location")
    private String location;

    @NotNull
    @Column(name="workEmail")
    private String workEmail;

    @Column(name="insperityId")
    private String insperityId; 

    @Column(name="startDate")
    private Date startDate;

    @Column(name="bioText")
    private String bioText;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getBioText() {
        return bioText;
    }

    public void setBioText(String bioText) {
        this.bioText = bioText;
    }
}
