package com.objectcomputing.checkins.services.checkin_notes;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
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
@Table(name = "checkin_notes")
public class CheckinNote {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of checkin notes")
    private UUID id;

    @NotNull
    @Column(name = "checkinid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the checkin this entry is associated with")
    private UUID checkinid;

    @NotNull
    @Column(name = "createdbyid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with")
    private UUID createdbyid;

    @Nullable
    @Column(name = "description")
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Schema(description = "description of the check in note")
    private String description;

    public CheckinNote(UUID id, UUID checkinid, UUID createdbyid, @Nullable String description) {
        this.id = id;
        this.checkinid = checkinid;
        this.createdbyid = createdbyid;
        this.description = description;
    }

    public CheckinNote(UUID checkinid, UUID createdbyid, @Nullable String description) {
        this(null, checkinid, createdbyid, description);
    }

    @Override
    public String toString() {
        return "CheckinNotes{" +
                "id=" + id +
                ", checkinid=" + checkinid +
                ", createdbyid=" + createdbyid +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckinNote that = (CheckinNote) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(checkinid, that.checkinid) &&
                Objects.equals(createdbyid, that.createdbyid) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkinid, createdbyid, description);
    }

}

