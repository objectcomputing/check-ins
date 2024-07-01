package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
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
    private Boolean publiclyVisible;

    @Nullable
    private Team recipientTeam;

    @NotNull
    private List<MemberProfile> recipientMembers;

    public List<MemberProfile> getRecipientMembers() {
        return recipientMembers;
    }

    public void setRecipientMembers(List<MemberProfile> recipientMembers) {
        this.recipientMembers = recipientMembers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KudosResponseDTO that = (KudosResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(message, that.message) && Objects.equals(publiclyVisible, that.publiclyVisible) && Objects.equals(senderId, that.senderId) && Objects.equals(dateCreated, that.dateCreated) && Objects.equals(dateApproved, that.dateApproved) && Objects.equals(recipientTeam, that.recipientTeam) && Objects.equals(recipientMembers, that.recipientMembers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, senderId, dateCreated, dateApproved, recipientTeam, recipientMembers, publiclyVisible);
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
                ", publiclyVisible=" + publiclyVisible +
                '}';
    }
}
