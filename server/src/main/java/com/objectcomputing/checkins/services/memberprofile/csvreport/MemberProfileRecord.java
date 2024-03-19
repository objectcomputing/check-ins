package com.objectcomputing.checkins.services.memberprofile.csvreport;

import io.micronaut.core.annotation.Introspected;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "member_profile_record")
public class MemberProfileRecord {

    @Id
    private UUID id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "title")
    private String title;

    @Column(name = "location")
    private String location;

    @Column(name = "workemail")
    private String workEmail;

    @Column(name = "startdate")
    private LocalDate startDate;

    @Column(name = "tenure")
    private String tenure;

    @Column(name = "pdlname")
    private String pdlName;

    @Column(name = "pdlemail")
    private String pdlEmail;

    @Column(name = "supervisorname")
    private String supervisorName;

    @Column(name = "supervisoremail")
    private String supervisorEmail;

    public MemberProfileRecord(String firstName, String lastName, String title, String location,
                               String workEmail, LocalDate startDate, String tenure, String pdlName, String pdlEmail,
                               String supervisorName, String supervisorEmail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.location = location;
        this.workEmail = workEmail;
        this.startDate = startDate;
        this.tenure = tenure;
        this.pdlName = pdlName;
        this.pdlEmail = pdlEmail;
        this.supervisorName = supervisorName;
        this.supervisorEmail = supervisorEmail;
    }

    public MemberProfileRecord() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getTenure() {
        return tenure;
    }

    public void setTenure(String tenure) {
        this.tenure = tenure;
    }

    public String getPdlName() {
        return pdlName;
    }

    public void setPdlName(String pdlName) {
        this.pdlName = pdlName;
    }

    public String getPdlEmail() {
        return pdlEmail;
    }

    public void setPdlEmail(String pdlEmail) {
        this.pdlEmail = pdlEmail;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisorEmail() {
        return supervisorEmail;
    }

    public void setSupervisorEmail(String supervisorEmail) {
        this.supervisorEmail = supervisorEmail;
    }
}
