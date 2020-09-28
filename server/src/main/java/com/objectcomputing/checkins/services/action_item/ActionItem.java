package com.objectcomputing.checkins.services.action_item;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.CustomRecord;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Entity
@Table(name = ActionItem.ACTION_ITEMS)
public class ActionItem {

    public static final String ACTION_ITEMS = "action_items";
    public static final org.jooq.Table<Record> ACTION_ITEMS_TABLE = table(ACTION_ITEMS);
    public static final String ID_FIELD = "id";
    public static final Field<UUID> ACTION_ITEMS_DOT_ID = field(ACTION_ITEMS.concat(".").concat(ID_FIELD), UUID.class);
    public static final String CHECKIN_ID_FIELD = "checkinid";
    public static final Field<UUID> ACTION_ITEMS_DOT_CHECKIN_ID = field(ACTION_ITEMS.concat(".").concat(CHECKIN_ID_FIELD), UUID.class);
    public static final String CREATED_BY_ID_FIELD = "createdbyid";
    public static final Field<UUID> ACTION_ITEMS_DOT_CREATED_BY_ID = field(ACTION_ITEMS.concat(".").concat(CREATED_BY_ID_FIELD), UUID.class);
    public static final String DESCRIPTION_FIELD = "description";
    public static final Field<String> ACTION_ITEMS_DOT_DESCRIPTION = field(ACTION_ITEMS.concat(".").concat(DESCRIPTION_FIELD), String.class);
    public static final String PRIORITY_FIELD = "priority";
    public static final Field<Double> ACTION_ITEMS_DOT_PRIORITY = field(ACTION_ITEMS.concat(".").concat(PRIORITY_FIELD), Double.class);

    @Id
    @Column(name = ID_FIELD)
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this action item", required = true)
    private UUID id;

    @NotNull
    @Column(name = CHECKIN_ID_FIELD)
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the checkin this entry is associated with", required = true)
    private UUID checkinid;

    @NotNull
    @Column(name = CREATED_BY_ID_FIELD)
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID createdbyid;

    @Nullable
    @Column(name = DESCRIPTION_FIELD)
    @Schema(description = "description of the action item")
    private String description;

    @Column(name = PRIORITY_FIELD)
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

    public static ActionItem fromRecord(Record record) {
        return new ActionItem(record.get(ID_FIELD, UUID.class),
                record.get(CHECKIN_ID_FIELD, UUID.class),
                record.get(CREATED_BY_ID_FIELD, UUID.class),
                record.get(DESCRIPTION_FIELD, String.class),
                record.get(PRIORITY_FIELD, Double.class));
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
