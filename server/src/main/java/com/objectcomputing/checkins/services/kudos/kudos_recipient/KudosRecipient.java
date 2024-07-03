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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Introspected
@AllArgsConstructor
@NoArgsConstructor
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

    /**
     * Constructor for creating KudosRecipient
     * @param kudosId id of the kudos being given to this recipient
     * @param memberId id of the member receiving the kudos
     */
    public KudosRecipient(UUID kudosId, UUID memberId) {
        this(null, kudosId, memberId);
    }
}
