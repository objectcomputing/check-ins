package com.objectcomputing.checkins.services.volunteering;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Introspected
public class VolunteeringOrganizationDTO {

    @NotBlank
    @Schema(description = "name of the volunteering organization")
    private String name;

    @NotBlank
    @Schema(description = "description of the volunteering organization")
    private String description;

    @NotBlank
    @Schema(description = "website for the volunteering organization")
    private String website;

    @Schema(description = "whether the Volunteering Organization is active")
    private Boolean active;

    public VolunteeringOrganizationDTO(@NotBlank String name, @NotBlank String description, @NotBlank String website) {
        this(name, description, website, true);
    }
}
