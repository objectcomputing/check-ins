package com.objectcomputing.checkins.services.github;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Introspected
public class IssueCreateDTO {

    @NotBlank
    @Schema(title = "The title of the new Github issue")
    private String title;

    @NotBlank
    @Schema(title = "The description of the issue")
    private String body;

    private String[] labels = {"bug"};
}
