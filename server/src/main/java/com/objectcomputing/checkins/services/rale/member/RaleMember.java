package com.objectcomputing.checkins.services.rale.member;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import io.micronaut.core.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "rale_member")
public class RaleMember {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of this member to rale entry", required = true)
    private UUID id;

    @NotNull
    @Column(name = "raleid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the rale this entry is associated with", required = true)
    private UUID raleId;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member this entry is associated with", required = true)
    private UUID memberId;

    @Nullable
    @Column(name = "lead")
    @Schema(description = "whether member is lead or not represented by true or false respectively",
            nullable = true)
    private Boolean lead;

    public RaleMember(UUID raleId, UUID memberId, Boolean lead) {
        this(null, raleId, memberId, lead);
    }

    public RaleMember(UUID id, UUID raleId, UUID memberId, Boolean lead) {
        this.id = id;
        this.raleId = raleId;
        this.memberId = memberId;
        this.lead = lead;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRaleId() {
        return raleId;
    }

    public void setRaleId(UUID raleId) {
        this.raleId = raleId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public boolean isLead() {
        return lead != null && lead;
    }

    public void setLead(boolean lead) {
        this.lead = lead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaleMember that = (RaleMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(raleId, that.raleId) &&
                Objects.equals(memberId, that.memberId) &&
                Objects.equals(lead, that.lead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, raleId, memberId, lead);
    }

    @Override
    public String toString() {
        return "RaleMember{" +
                "id=" + id +
                ", raleId=" + raleId +
                ", memberId=" + memberId +
                ", lead=" + isLead() +
                '}';
    }
}