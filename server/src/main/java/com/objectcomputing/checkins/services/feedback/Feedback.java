package com.objectcomputing.checkins.services.feedback;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the entry the feedback is associated with", required = true)
    private UUID id;

    @Column(name = "content")
    @NotNull
    @Schema(description = "content of the feedback", required = true)
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(content::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    private String content;

    @Column(name = "sentTo")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of member profile to whom the feedback was sent", required = true)
    private UUID sentTo;

    @Column(name = "sentBy")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of member profile who created the feedback", required = true)
    private UUID sentBy;

    @Column(name = "confidential")
    @NotNull
    @TypeDef(type = DataType.BOOLEAN)
    @Schema(description = "whether the feedback is public or private", required = true)
    private Boolean confidential;

    @Column(name = "createdOn")
    @NotNull
    @Schema(description = "date when the feedback was created", required = true)
    private LocalDateTime createdOn;

    @Column(name = "updatedOn")
    @Nullable
    @Schema(description = "date of the latest update to the feedback", required = true)
    private LocalDateTime updatedOn;

    public Feedback(@NotNull String content,
                    @NotNull UUID sendTo,
                    @NotNull UUID sendBy,
                    @NotNull Boolean confidential,
                    @NotNull LocalDateTime createdOn,
                    @Nullable LocalDateTime updatedOn) {
        this(null, content, sendTo, sendBy, confidential, createdOn, updatedOn);
    }

    public Feedback(@Nullable UUID id,
                    @NotNull String content,
                    @NotNull UUID sendTo,
                    @NotNull UUID sendBy,
                    @NotNull Boolean confidential,
                    @NotNull LocalDateTime createdOn,
                    @Nullable LocalDateTime updatedOn) {
        this.id = id;
        this.content = content;
        this.sentTo = sendTo;
        this.sentBy = sendBy;
        this.confidential = confidential;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getSentTo() {
        return sentTo;
    }

    public void setSentTo(UUID sendTo) {
        this.sentTo = sendTo;
    }

    public UUID getSentBy() {
        return sentBy;
    }

    public void setSentBy(UUID sendBy) {
        this.sentBy = sendBy;
    }

    public Boolean getConfidential() {
        return confidential;
    }

    public void setConfidential(Boolean confidential) {
        this.confidential = confidential;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feedback that = (Feedback) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(content, that.content) &&
                Objects.equals(sentTo, that.sentTo) &&
                Objects.equals(sentBy, that.sentBy) &&
                Objects.equals(confidential, that.confidential) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(updatedOn, that.updatedOn);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", content=" + content +
                ", sendTo=" + sentTo +
                ", sendBy=" + sentBy +
                ", confidential=" + confidential +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, sentTo, sentBy, confidential, createdOn, updatedOn);
    }
}
