package com.objectcomputing.checkins.services.checkindocument;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="checkin_document")
public class CheckinDocument {
    
    public CheckinDocument() {}

    public CheckinDocument(UUID checkinsId, String uploadDocId) {
        this.checkinsId = checkinsId;
        this.uploadDocId = uploadDocId;
    }
    
    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the checkin_document", required = true)
    private UUID id;

    @Column(name="checkinsId")
    @NotBlank
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the checkIn this entry is associated with", required = true)
    private UUID checkinsId;

    @Column(name="uploadDocId", unique = true)
    @NotBlank
    @Schema(description = "id of the uploaded document", required = true)
    private String uploadDocId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCheckinsId() {
        return checkinsId;
    }

    public void setCheckinsId(UUID checkinsId) {
        this.checkinsId = checkinsId;
    }

    public String getUploadDocId() {
        return uploadDocId;
    }

    public void setUploadDocId(String uploadDocId) {
        this.uploadDocId = uploadDocId;
    }

    @Override
    public String toString() {
        return "CheckinDocument{" +
                "id=" + id +
                ", checkinsId=" + checkinsId +
                ", uploadDocId=" + uploadDocId +
                '}';
    }
}
