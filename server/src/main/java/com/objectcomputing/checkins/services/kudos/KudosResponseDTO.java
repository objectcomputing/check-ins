package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Introspected
public class KudosResponseDTO {

    @NotNull
    private UUID id;

    @NotBlank
    private String message;

    @NotNull
    private UUID senderId;

    @NotNull
    private LocalDate dateCreated;

    @Nullable
    private LocalDate dateApproved;

    @Nullable
    private Team recipientTeam;

    @Nullable
    private List<UUID> recipientMembers;

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

    @Nullable
    public Team getRecipientTeam() {
        return recipientTeam;
    }

    public void setRecipientTeam(@Nullable Team recipientTeam) {
        this.recipientTeam = recipientTeam;
    }

    @Nullable
    public List<UUID> getRecipientMembers() {
        return recipientMembers;
    }

    public void setRecipientMembers(@Nullable List<UUID> recipientMembers) {
        this.recipientMembers = recipientMembers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KudosResponseDTO that = (KudosResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(message, that.message) && Objects.equals(senderId, that.senderId) && Objects.equals(dateCreated, that.dateCreated) && Objects.equals(dateApproved, that.dateApproved) && Objects.equals(recipientTeam, that.recipientTeam) && Objects.equals(recipientMembers, that.recipientMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, senderId, dateCreated, dateApproved, recipientTeam, recipientMembers);
    }

    @Override
    public String toString() {
        return "KudosResponseDTO{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", senderId=" + senderId +
                ", dateCreated=" + dateCreated +
                ", dateApproved=" + dateApproved +
                ", recipientTeam=" + recipientTeam +
                ", recipientMembers=" + recipientMembers +
                '}';
    }
}
