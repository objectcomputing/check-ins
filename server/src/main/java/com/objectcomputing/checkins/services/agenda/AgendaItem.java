package com.objectcomputing.checkins.services.agenda;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

@Entity
@Table(name ="agenda-item")
public class AgendaItem {

    public AgendaItem(UUID checkinId, UUID createdById) {
                        this.checkinId=checkinId;
                        this.createdById=createdById;
                        }

    public AgendaItem() {
    }

    @Id
    @Column(name="uuid")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    private UUID uuid;

    @Column(name="checkinId")
    @TypeDef(type=DataType.STRING)
    private UUID checkinId;

    @Column(name="createdById")
    @TypeDef(type=DataType.STRING)
    private UUID createdById;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getCheckinId() {
        return checkinId;
    }

    public void setCheckinId(UUID checkinId) {
        this.checkinId = checkinId;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setMemberId(UUID createdById) {
        this.createdById = createdById;
    }
    
}
