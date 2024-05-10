package com.objectcomputing.checkins.services.opportunities;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Introspected
public class OpportunitiesCreateDTO {

    @NotNull
    @Schema(description = "name of the opportunity")
    private String name;

    @NotNull
    @Schema(description = "description of the opportunity")
    private String description;

    @NotNull
    @Schema(description = "link to the url")
    private String url;

    @NotNull
    @Schema(description = "date for expiresOn")
    private LocalDate expiresOn;

    @NotNull
    @Schema(description = "state of the associated opportunity")
    private Boolean pending;

}
