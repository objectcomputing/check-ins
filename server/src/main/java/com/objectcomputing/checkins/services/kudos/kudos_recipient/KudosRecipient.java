package com.objectcomputing.checkins.services.kudos.kudos_recipient;

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
import java.util.Objects;
import java.util.UUID;

@Entity
@Introspected
@Table(name = "kudos_recipient")
public class KudosRecipient {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the kudos recipient entity")
    private UUID id;

    @NotNull
    @Column(name = "kudosid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the kudos being given to this recipient")
    private UUID kudosId;

    @NotNull
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member receiving the kudos")
    private UUID memberId;

    public KudosRecipient() {}

    /**
     * Constructor for creating KudosRecipient
     * @param kudosId id of the kudos being given to this recipient
     * @param memberId id of the member receiving the kudos
     */
    public KudosRecipient(UUID kudosId, UUID memberId) {
        this.kudosId = kudosId;
        this.memberId = memberId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getKudosId() {
        return kudosId;
    }

    public void setKudosId(UUID kudosId) {
        this.kudosId = kudosId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KudosRecipient that = (KudosRecipient) o;
        return Objects.equals(id, that.id) && Objects.equals(kudosId, that.kudosId) && Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kudosId, memberId);
    }

    @Override
    public String toString() {
        return "KudosRecipient{" +
                "id=" + id +
                ", kudosId=" + kudosId +
                ", memberId=" + memberId +
                '}';
    }
}
