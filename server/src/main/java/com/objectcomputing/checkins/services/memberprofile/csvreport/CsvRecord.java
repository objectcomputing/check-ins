package com.objectcomputing.checkins.services.memberprofile.csvreport;

import java.time.LocalDate;

public class CsvRecord {

    private final String firstName;
    private final String lastName;
    private final String title;
    private final String location;
    private final String workEmail;
    private final LocalDate startDate;
    private final String tenure;
    private final String pdlName;
    private final String supervisorName;
    private final String pdlEmail;
    private final String supervisorEmail;

    public CsvRecord(String firstName, String lastName, String title, String location, String workEmail,
                     LocalDate startDate, String tenure, String pdlName, String pdlEmail, String supervisorName, String supervisorEmail) {
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getTenure() {
        return tenure;
    }

    public String getPdlName() {
        return pdlName;
    }

    public String getPdlEmail() { return pdlEmail; }

    public String getSupervisorName() {
        return supervisorName;
    }

    public String getSupervisorEmail() { return supervisorEmail; }
}
