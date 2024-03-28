package com.objectcomputing.checkins.services.agenda_item;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "agenda_items")
public class AgendaItem {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of agenda item", required = true)
    private UUID id;

    @NotNull
    @Column(name = "checkinid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the checkin this entry is associated with", required = true)
    private UUID checkinid;

    @NotNull
    @Column(name = "createdbyid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID createdbyid;

    @Nullable
    @ColumnTransformer(
            read = "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Column(name = "description")
    @Schema(description = "description of the agenda item")
    private String description;

    @NotNull
    @Column(name = "priority")
    @Schema(description = "Allow for a user defined display order")
    private Double priority;

    public AgendaItem(UUID checkinid, UUID createdbyid, String description) {
        this(null, checkinid, createdbyid, description);
    }

    public AgendaItem(UUID id, UUID checkinid, UUID createdbyid, String description) {
        this(id, checkinid, createdbyid, description, 1.0);
    }

    public AgendaItem() {}

    public AgendaItem(UUID id, UUID checkinid, UUID createdbyid, @Nullable String description, Double priority) {
        this.id = id;
        this.checkinid = checkinid;
        this.createdbyid = createdbyid;
        this.description = description;
        this.priority = priority;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCheckinid() {
        return this.checkinid;
    }

    public void setCheckinid(UUID checkinid) {
        this.checkinid = checkinid;
    }

    public UUID getCreatedbyid() {
        return this.createdbyid;
    }

    public void setCreatedbyid(UUID createdbyid) {
        this.createdbyid = createdbyid;
    }

    @Nullable
    public String getDescription() {
        return this.description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @SuppressWarnings("unused")
    public Double getPriority() {
        return priority;
    }

    public void setPriority(Double priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "AgendaItems{" +
                "id=" + id +
                ", checkinid=" + checkinid +
                ", createdbyid=" + createdbyid +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgendaItem that = (AgendaItem) o;
        return Double.compare(that.priority, priority) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(checkinid, that.checkinid) &&
                Objects.equals(createdbyid, that.createdbyid) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkinid, createdbyid, description, priority);
    }

}

