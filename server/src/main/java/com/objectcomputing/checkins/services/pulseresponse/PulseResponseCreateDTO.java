package com.objectcomputing.checkins.services.pulseresponse;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class PulseResponseCreateDTO {

    @NotNull
    @Schema(description = "date for submissionDate")
    private LocalDate submissionDate;

    @NotNull
    @Schema(description = "date for updatedDate")
    private LocalDate updatedDate;
    
    @NotNull
    @Schema(description = "id of the associated member")
    private UUID teamMemberId;

    @NotNull
    @Schema(description = "description of internal feelings")
    private String internalFeelings;

    @NotNull
    @Schema(description = "description of external feelings")
    private String externalFeelings;

}
