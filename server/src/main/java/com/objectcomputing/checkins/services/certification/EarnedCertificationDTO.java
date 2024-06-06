package com.objectcomputing.checkins.services.certification;

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
class EarnedCertificationDTO {

    @NotNull
    @Schema(description = "ID of the member being certified")
    private UUID memberId;

    @NotNull
    @Schema(description = "id of the certification")
    private UUID certificationId;

    @NotNull
    @Schema(description = "description of the certification earned")
    private String description;

    @NotNull
    @Schema(description = "when the certification was earned")
    private LocalDate earnedDate;

    @Schema(description = "optionally when the certification expires")
    private LocalDate expirationDate;

    @Schema(description = "optionally the image of the certification")
    private String certificateImageUrl;
}
