package com.objectcomputing.checkins.services.certification;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Introspected
class CertificationDTO {

    @NotNull
    @Schema(description = "name of the certification")
    private String name;

    @NotBlank
    @Schema(description = "description of the certification")
    private String description;

    @Nullable
    @Schema(description = "badge url of the certification")
    private String badgeUrl;

    @Nullable
    @Schema(description = "whether the Certification is active")
    private Boolean active;

    CertificationDTO(String name, String description, String badgeUrl) {
        this(name, description, badgeUrl, true);
    }
}
