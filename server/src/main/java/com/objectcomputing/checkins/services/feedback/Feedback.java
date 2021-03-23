package com.objectcomputing.checkins.services.feedback;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

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
    @Schema(description = "id of the feedback", required = true)
    private UUID id;

    @Column(name = "content")
    @NotNull
    @Schema(description = "content of the feedback", required = true)
    private String content;

    @Column(name = "sendTo")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of team member to whom the feedback was sent", required = true)
    private UUID sendTo;

    @Column(name = "sendBy")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of team member who created the feedback", required = true)
    private UUID sendBy;

    @Column(name = "createdOn")
    @NotNull
    @Schema(description = "date when the feedback was created", required = true)
    private LocalDateTime createdOn;

    @Column(name = "updatedOn")
    @NotNull
    @Schema(description = "date of the latest update to the feedback", required = true)
    private LocalDateTime updatedOn;

    public Feedback() {

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

    public UUID getSendTo() {
        return sendTo;
    }

    public void setSendTo(UUID sendTo) {
        this.sendTo = sendTo;
    }

    public UUID getSendBy() {
        return sendBy;
    }

    public void setSendBy(UUID sendBy) {
        this.sendBy = sendBy;
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
                Objects.equals(sendTo, that.sendTo) &&
                Objects.equals(sendBy, that.sendBy) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(updatedOn, that.updatedOn);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", content=" + content +
                ", sendTo=" + sendTo +
                ", sendBy=" + sendBy +
                ", createdOn=" + createdOn +
                ", updatedOn=" + updatedOn +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, sendTo, sendBy, createdOn, updatedOn);
    }
}
