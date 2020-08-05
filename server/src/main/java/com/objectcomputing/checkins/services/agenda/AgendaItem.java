package com.objectcomputing.checkins.services.agenda;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "agenda_items")
public class AgendaItem {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this agenda item", required = true)
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
    @Column(name = "description")
    @Schema(description = "description of the agenda item")
    private String description;

    public AgendaItem(@JsonProperty("checkinid") @TypeDef(type = DataType.STRING) @Valid @NotNull UUID checkinid,
                      @JsonProperty("createdbyid") @TypeDef(type = DataType.STRING) @Valid @NotNull UUID createdbyid,
                      @JsonProperty("description") String description) {
        this(null, checkinid, createdbyid, description);
    }

    public AgendaItem(UUID id, UUID checkinid, UUID createdbyid, String description) {
        this.id = id;
        this.checkinid = checkinid;
        this.createdbyid = createdbyid;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCheckinid() {
        return checkinid;
    }

    public void setCheckinid(UUID checkinid) {
        this.checkinid = checkinid;
    }

    public UUID getCreatedbyid() {
        return createdbyid;
    }

    public void setCreatedbyid(UUID createdbyid) {
        this.createdbyid = createdbyid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AgendaItem{" +
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
        AgendaItem that = (AgendaItem) o;
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
