package com.objectcomputing.checkins.services.checkinnotes;

import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name="checkin_notes")
public class CheckinNote {

    public CheckinNote(UUID checkinid, UUID createdbyid, boolean privateNotes,String description){
        this.checkinid=checkinid;
        this.createdbyid=createdbyid;
        this.privateNotes=privateNotes;
        this.description=description;
    }

    @Id
    @Column(name = "uuid")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "UUID of checkin notes", required = true)
    private UUID uuid;

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

    @NotNull
    @Column(name="privatenotes")
    @Schema(description = "boolean flag to mark if notes are private", required = true)
    boolean privateNotes ;

    @Nullable
    @Column(name = "description")
    @Schema(description = "description of the action item")
    private String description;


    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getPrivateNotes() {
        return privateNotes;
    }

    public void setPrivateNotes(boolean privateNotes) {
        this.privateNotes = privateNotes;
    }

    @Override
    public String toString() {
        return "CheckinNotes{" +
                "id=" + uuid +
                ", checkinid=" + checkinid +
                ", createdbyid=" + createdbyid +
                ", privateNotes=" + privateNotes +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckinNote that = (CheckinNote) o;
        return Objects.equals(uuid, that.uuid) &&
                Objects.equals(checkinid, that.checkinid) &&
                Objects.equals(createdbyid, that.createdbyid) &&
                Objects.equals(privateNotes, that.privateNotes)&&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, checkinid, createdbyid, privateNotes,description);
    }

}

