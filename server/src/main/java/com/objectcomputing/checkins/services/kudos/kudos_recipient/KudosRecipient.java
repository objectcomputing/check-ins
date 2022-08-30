package com.objectcomputing.checkins.services.kudos.kudos_recipient;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
    @Schema(description = "the id of the kudos recipient entity", required = true)
    private UUID id;

    @NotNull
    @Column(name = "kudosid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the kudos being given to this recipient")
    private UUID kudosId;

    @Nullable
    @Column(name = "memberid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the member receiving the kudos")
    private UUID memberId;

    @Nullable
    @Column(name = "teamid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the team receiving the kudos")
    private UUID teamId;

    public KudosRecipient() {}

    /**
     * Constructor for creating KudosRecipient
     * @param kudosId id of the kudos being given to this recipient
     * @param memberId id of the member receiving the kudos
     * @param teamId id of the team receiving the kudos
     */
    public KudosRecipient(UUID kudosId, @Nullable UUID memberId, @Nullable UUID teamId) {
        this.kudosId = kudosId;
        this.memberId = memberId;
        this.teamId = teamId;
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

    @Nullable
    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(@Nullable UUID memberId) {
        this.memberId = memberId;
    }

    @Nullable
    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(@Nullable UUID teamId) {
        this.teamId = teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KudosRecipient that = (KudosRecipient) o;
        return Objects.equals(id, that.id) && Objects.equals(kudosId, that.kudosId) && Objects.equals(memberId, that.memberId) && Objects.equals(teamId, that.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kudosId, memberId, teamId);
    }

    @Override
    public String toString() {
        return "KudosRecipient{" +
                "id=" + id +
                ", kudosId=" + kudosId +
                ", memberId=" + memberId +
                ", teamId=" + teamId +
                '}';
    }
}
