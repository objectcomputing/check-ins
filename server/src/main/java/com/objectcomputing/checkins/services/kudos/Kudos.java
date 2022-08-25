package com.objectcomputing.checkins.services.kudos;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "kudos")
public class Kudos {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the id of the kudos", required = true)
    private UUID id;

    @NotBlank
    @Column(name = "message")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(message::bytea, '${aes.key}')",
            write = "pgp_sym_encrypt(?, '${aes.key}')"
    )
    @Schema(description = "message describing the kudos", required = true)
    private String message;

    @NotNull
    @Column(name = "senderid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the user who gave the kudos", required = true)
    private UUID senderId;

    @NotNull
    @Column(name = "recipientid")
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the user who received the kudos", required = true)
    private UUID recipientId;

    @Column(name = "datecreated")
    @DateCreated
    @TypeDef(type = DataType.DATE)
    @Schema(description = "date the kudos were created", required = true)
    private LocalDate dateCreated;

    @Nullable
    @Column(name = "dateapproved")
    @TypeDef(type = DataType.DATE)
    @Schema(description = "date the kudos were approved, null if pending")
    private LocalDate dateApproved;

    public Kudos() {}

    /**
     * Constructor for creating Kudos
     * @param message string describing the kudos
     * @param senderId id of the user who gave the kudos
     * @param recipientId id of the user who received the kudos
     */
    public Kudos(String message, UUID senderId, UUID recipientId) {
        this.message = message;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.dateApproved = null;
    }

    /**
     * Constructor for updating Kudos
     * @param id the id of the kudos
     * @param message string describing the kudos
     * @param senderId id of the user who gave the kudos
     * @param recipientId id of the user who received the kudos
     * @param dateApproved date the kudos were approved
     */
    public Kudos(UUID id, String message, UUID senderId, UUID recipientId, @Nullable LocalDate dateApproved) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.dateApproved = dateApproved;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Nullable
    public LocalDate getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(@Nullable LocalDate dateApproved) {
        this.dateApproved = dateApproved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kudos kudos = (Kudos) o;
        return Objects.equals(id, kudos.id) && Objects.equals(message, kudos.message) && Objects.equals(senderId, kudos.senderId) && Objects.equals(recipientId, kudos.recipientId) && Objects.equals(dateCreated, kudos.dateCreated) && Objects.equals(dateApproved, kudos.dateApproved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, senderId, recipientId, dateCreated, dateApproved);
    }

    @Override
    public String toString() {
        return "Kudos{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", dateCreated=" + dateCreated +
                ", dateApproved=" + dateApproved +
                '}';
    }
}
