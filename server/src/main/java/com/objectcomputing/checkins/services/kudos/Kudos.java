package com.objectcomputing.checkins.services.kudos;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "kudos")
public class Kudos {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the kudos")
    private UUID id;

    @NotBlank
    @Column(name = "message")
    @ColumnTransformer(read = "pgp_sym_decrypt(message::bytea, '${aes.key}')", write = "pgp_sym_encrypt(?, '${aes.key}')")
    @Schema(description = "message describing the kudos")
    private String message;

    @Nullable
    @Column(name = "publiclyvisible")
    @Schema(description = "true if the kudos is public")
    private Boolean publiclyVisible;

    @NotNull
    @Column(name = "senderid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the user who gave the kudos")
    private UUID senderId;

    @Nullable
    @Column(name = "teamid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the team that received the kudos, null if anonymous group of members")
    private UUID teamId;

    @Column(name = "datecreated")
    @DateCreated
    @TypeDef(type = DataType.DATE)
    @Schema(description = "date the kudos were created")
    private LocalDate dateCreated;

    @Nullable
    @Column(name = "dateapproved")
    @TypeDef(type = DataType.DATE)
    @Schema(description = "date the kudos were approved, null if pending")
    private LocalDate dateApproved;

    public Kudos() {
    }

    Kudos(KudosCreateDTO kudosCreateDTO) {
        this.message = kudosCreateDTO.getMessage();
        this.senderId = kudosCreateDTO.getSenderId();
        this.teamId = kudosCreateDTO.getTeamId();
        this.publiclyVisible = kudosCreateDTO.isPubliclyVisible();
    }
}
