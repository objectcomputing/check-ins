package com.objectcomputing.checkins.services.survey;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@Introspected
public class SurveyCreateDTO {

    @NotNull
    @Schema(description = "description of internal feelings")
    private String name;

    @NotNull
    @Schema(description = "description of external feelings")
    private String description;

    @NotNull
    @Schema(description = "date for createdOn")
    private LocalDate createdOn;

    @NotNull
    @Schema(description = "id of the associated member")
    private UUID createdBy;

}
