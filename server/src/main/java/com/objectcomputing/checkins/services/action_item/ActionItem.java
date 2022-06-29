package com.objectcomputing.checkins.services.action_item;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "action_items")
public class ActionItem {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this action item", required = true)
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
            read =  "pgp_sym_decrypt(description::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}') "
    )
    @Column(name="description")
    @Schema(description = "description of the action item")
    private String description;

    @Column(name = "priority")
    @Schema(description = "Allow for a user defined display order")
    private double priority;

    public ActionItem(UUID checkinid, UUID createdbyid, String description) {
        this(null, checkinid, createdbyid, description);
    }

    public ActionItem(UUID id, UUID checkinid, UUID createdbyid, String description) {
        this(id, checkinid, createdbyid, description, 1.0);
    }

    public ActionItem(UUID checkinid, UUID createdbyid, String description, double priority) {
        this(null, checkinid, createdbyid, description, priority);
    }

    public ActionItem(UUID id, UUID checkinid, UUID createdbyid, String description, double priority) {
        this.id = id;
        this.checkinid = checkinid;
        this.createdbyid = createdbyid;
        this.description = description;
        this.priority = priority;
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

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "ActionItem{" +
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
        ActionItem that = (ActionItem) o;
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
