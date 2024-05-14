package com.objectcomputing.checkins.services.checkindocument;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
@Introspected
@Table(name = "checkin_document")
public class CheckinDocument {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "the id of the checkin_document")
    private UUID id;

    @Column(name="checkinsid")
    @NotNull
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the checkIn this entry is associated with")
    private UUID checkinsId;

    @Column(name="uploaddocid", unique = true)
    @NotNull
    @Schema(description = "id of the uploaded document")
    private String uploadDocId;

    public CheckinDocument(UUID checkinsId, String uploadDocId) {
        this(null, checkinsId, uploadDocId);
    }

    public CheckinDocument(UUID id, UUID checkinsId, String uploadDocId) {
        this.id = id;
        this.checkinsId = checkinsId;
        this.uploadDocId = uploadDocId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckinDocument that = (CheckinDocument) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(checkinsId, that.checkinsId) &&
                Objects.equals(uploadDocId, that.uploadDocId);
    }

    @Override
    public String toString() {
        return "CheckinDocument{" +
                "id=" + id +
                ", checkinsId=" + checkinsId +
                ", uploadDocId=" + uploadDocId +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkinsId, uploadDocId);
    }
}
