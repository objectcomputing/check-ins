package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class AnniversaryReportResponseDTO {

    @NotBlank
    @Schema(description = "name this entry is associated with", required = true)
    private String name;

    @NotBlank
    @Schema(description = "anniversary date this entry is associated with", required = true)
    private String anniversary;

    @Schema(description = "years of service this entry is associated with")
    private double yearsOfService;

    @NotNull
    @Schema(description = "id of the member profile this entry is associated with", required = true)
    private UUID userId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(String anniversary) {
        this.anniversary = anniversary;
    }

    public double getYearsOfService() {
        return yearsOfService;
    }

    public void setYearsOfService(double yearsOfService) {
        this.yearsOfService = yearsOfService;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
