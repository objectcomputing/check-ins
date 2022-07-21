package com.objectcomputing.checkins.services.onboard.signrequest;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Introspected
public class SignRequestCreateDTO {

    @NotBlank
    @Schema(title = "The title of the new SignRequest request", required = true)
    private String title;

    @NotBlank
    @Schema(title = "The description of the request", required = true)
    private String body;

}
