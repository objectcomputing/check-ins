package com.objectcomputing.checkins.services.checkindocument;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Introspected
public class CheckinDocumentCreateDTO {

    @NotNull
    @Schema(description = "id of the associated checkIn")
    private UUID checkinsId;

    @NotNull
    @Schema(description = "id of the uploaded document")
    private String uploadDocId;

}
