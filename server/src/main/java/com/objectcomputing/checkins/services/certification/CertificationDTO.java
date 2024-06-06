package com.objectcomputing.checkins.services.certification;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Introspected
class CertificationDTO {

    @NotNull
    @Schema(description = "name of the certification")
    private String name;

    @NotNull
    @Schema(description = "badge url of the certification")
    private String badgeUrl;
}
