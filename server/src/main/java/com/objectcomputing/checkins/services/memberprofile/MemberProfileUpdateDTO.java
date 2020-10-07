package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.UUID;

@Introspected
public class MemberProfileUpdateDTO {

    @NotNull
    @Schema(description = "id of the member profile this entry is associated with", required = true)
    private UUID id;

    @NotBlank
    @Schema(description = "full name of the employee", required = true)
    private String name;

    @Nullable
    @Schema(description = "employee's title at the company")
    private String title ;

    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @Nullable
    @Schema(description = "where the employee is geographically located")
    private String location;

    @NotBlank
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjctComputing.com", required = true)
    private String workEmail;

    @Nullable
    @Schema(description = "unique identifier for this employee with the Insperity system")
    private String insperityId;

    @Nullable
    @Past
    @Schema(description = "employee's date of hire")
    private LocalDate startDate;

    @Nullable
    @Schema(description = "employee's biography")
    private String bioText;

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

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public UUID getPdlId() {
        return pdlId;
    }

    public void setPdlId(@Nullable UUID pdlId) {
        this.pdlId = pdlId;
    }

    @Nullable
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

    @Nullable
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
