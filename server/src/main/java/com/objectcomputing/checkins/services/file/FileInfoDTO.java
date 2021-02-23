package com.objectcomputing.checkins.services.file;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class FileInfoDTO {

    @NotNull
    @Schema(description = "id of file", required = true)
    private String fileId;

    @NotNull
    @Schema(description = "CheckIn id associated with the file", required = true)
    private UUID checkInId;

    @NotNull
    @Schema(description = "name of the file", required = true)
    private String name;

    @NotNull
    @Schema(description = "size of the file", required = true)
    private Long size;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public UUID getCheckInId() {
        return checkInId;
    }

    public void setCheckInId(UUID checkInId) {
        this.checkInId = checkInId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
