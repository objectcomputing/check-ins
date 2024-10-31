package com.objectcomputing.checkins.services.memberprofile.anniversaryreport;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class AnniversaryReportResponseDTO {

    @NotBlank
    @Schema(description = "name this entry is associated with")
    private String name;

    @NotBlank
    @Schema(description = "anniversary date this entry is associated with")
    private String anniversary;

    @NotNull
    @Schema(description = "years of service this entry is associated with")
    private Double yearsOfService;

    @NotNull
    @Schema(description = "id of the member profile this entry is associated with")
    private UUID userId;

}
