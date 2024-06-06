package com.objectcomputing.checkins.services.certification;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Introspected
class CertificationMergeDTO {

    @NotNull
    @Schema(description = "the ID of the certification to remove")
    private UUID sourceId;

    @NotNull
    @Schema(description = "the ID of the certification to move all the earned certificates to")
    private UUID targetId;
}
