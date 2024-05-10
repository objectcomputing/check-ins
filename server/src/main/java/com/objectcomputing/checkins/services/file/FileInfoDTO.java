package com.objectcomputing.checkins.services.file;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class FileInfoDTO {

    @NotNull
    @Schema(description = "id of file")
    private String fileId;

    @NotNull
    @Schema(description = "CheckIn id associated with the file")
    private UUID checkInId;

    @NotNull
    @Schema(description = "name of the file")
    private String name;

    @NotNull
    @Schema(description = "size of the file")
    private Long size;

}
