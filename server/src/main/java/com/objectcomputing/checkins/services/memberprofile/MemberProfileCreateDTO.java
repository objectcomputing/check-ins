package com.objectcomputing.checkins.services.memberprofile;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.UUID;

public class MemberProfileCreateDTO {

    @NotBlank
    @Schema(description = "full name of the employee", required = true)
    private String name;

    @NotBlank
    @Schema(description = "employee's role at the company", required = true)
    private String role ;

    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @NotBlank
    @Schema(description = "where the employee is geographically located", required = true)
    private String location;

    @NotBlank
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjctComputing.com", required = true)
    private String workEmail;

    @Nullable
    @Schema(description = "unique identifier for this employee with the Insperity system")
    private String insperityId;

    @NotNull
    @Past
    @Schema(description = "employee's date of hire", required = true)
    private LocalDate startDate;

    @Nullable
    @Schema(description = "employee's biography")
    private String bioText;

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

    @Nullable
    public UUID getPdlId() {
        return pdlId;
    }

    public void setPdlId(@Nullable UUID pdlId) {
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

    @Nullable
    public String getInsperityId() {
        return insperityId;
    }

    public void setInsperityId(@Nullable String insperityId) {
        this.insperityId = insperityId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Nullable
    public String getBioText() {
        return bioText;
    }

    public void setBioText(@Nullable String bioText) {
        this.bioText = bioText;
    }
}
