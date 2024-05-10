package com.objectcomputing.checkins.services.memberprofile;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class MemberProfileResponseDTO {

    @NotNull
    @Schema(description = "id of the member profile this entry is associated with")
    private UUID id;

    @NotBlank
    @Schema(description = "first name of the employee")
    private String firstName;

    @Nullable
    @Schema(description = "middle name of the employee")
    private String middleName;

    @NotBlank
    @Schema(description = "last name of the employee")
    private String lastName;

    @Nullable
    @Schema(description = "suffix name of the employee")
    private String suffix;

    @NotBlank
    @Schema(description = "full name of the employee")
    private String name;

    @NotBlank
    @Schema(description = "employee's title at the company")
    private String title ;

    @Nullable
    @Schema(description = "employee's professional development lead")
    private UUID pdlId;

    @NotBlank
    @Schema(description = "where the employee is geographically located")
    private String location;

    @NotBlank
    @Schema(description = "employee's OCI email. Typically last name + first initial @ObjectComputing.com")
    private String workEmail;

    @Nullable
    @Schema(description = "unique identifier for this employee")
    private String employeeId;

    @NotNull
    @Schema(description = "employee's date of hire")
    private LocalDate startDate;

    @Nullable
    @Schema(description = "employee's biography")
    private String bioText;

    @Nullable
    @Schema(description = "id of the supervisor this member is associated with", nullable = true)
    private UUID supervisorid;

    @Nullable
    @Schema(description = "employee's date of termination", nullable = true)
    private LocalDate terminationDate;

    @Nullable
    @Schema(description = "Birth date of employee", nullable = true)
    private LocalDate birthDay;

    @Nullable
    @Schema(description = "The employee termination was voluntary", nullable = true)
    private Boolean voluntary;

    @Nullable
    @Schema(description = "The employee is excluded from retention reports", nullable = true)
    private Boolean excluded;

    @Override
    public String toString() {
        return "MemberProfileResponseDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", pdlId=" + pdlId +
                ", location='" + location + '\'' +
                ", workEmail='" + workEmail + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", startDate=" + startDate +
                ", bioText='" + bioText + '\'' +
                ", supervisorid=" + supervisorid +
                ", terminationDate=" + terminationDate +
                ", birthDay=" + birthDay +
                ", voluntary=" + voluntary +
                ", excluded=" + excluded +
                '}';
    }
}
