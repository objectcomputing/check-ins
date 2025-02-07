package com.objectcomputing.checkins.services.slack.kudos;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "automated_kudos")
public class AutomatedKudos {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the kudos")
    private UUID id;

    @Column(name = "requested")
    @NotNull
    @Schema(description = "Has permission been requested of the poster")
    private Boolean requested;

    @NotBlank
    @Column(name = "message")
    @ColumnTransformer(read = "pgp_sym_decrypt(message::bytea, '${aes.key}')", write = "pgp_sym_encrypt(?, '${aes.key}')")
    @Schema(description = "message describing the kudos")
    private String message;

    @NotBlank
    @Column(name = "externalid")
    @ColumnTransformer(read = "pgp_sym_decrypt(message::bytea, '${aes.key}')", write = "pgp_sym_encrypt(?, '${aes.key}')")
    @Schema(description = "the external id of the sender")
    private String externalId;

    @NotNull
    @Column(name = "senderid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the user who gave the kudos")
    private UUID senderId;

    @Column(name = "recipientids")
    @TypeDef(type = DataType.STRING_ARRAY)
    @Schema(description = "UUIDs of the recipients")
    private List<String> recipientIds;

    // This is necessary for Micronaut to persist instances of this class.
    AutomatedKudos() {}

    AutomatedKudos(AutomatedKudosDTO automatedKudosDTO) {
        this.requested = false;
        this.message = automatedKudosDTO.getMessage();
        this.externalId = automatedKudosDTO.getExternalId();
        this.senderId = automatedKudosDTO.getSenderId();
        this.recipientIds = automatedKudosDTO.getRecipientIds()
                                             .stream()
                                             .map(UUID::toString)
                                             .collect(Collectors.toList());
    }
}
