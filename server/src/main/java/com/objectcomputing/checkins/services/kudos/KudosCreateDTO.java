package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Introspected
public class KudosCreateDTO {

    @NotBlank
    private String message;

    @NotNull
    private UUID senderId;

    @Nullable
    private UUID teamId;

    @NotNull
    private Boolean publiclyVisible;

    @NotNull
    private List<MemberProfile> recipientMembers;

    public KudosCreateDTO(Boolean publiclyVisible, String message, UUID senderId, @Nullable UUID teamId, List<MemberProfile> recipientMembers) {
        this.publiclyVisible = publiclyVisible;
        this.message = message;
        this.senderId = senderId;
        this.teamId = teamId;
        this.recipientMembers = recipientMembers;
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

    @Nullable
    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(@Nullable UUID teamId) {
        this.teamId = teamId;
    }

    public List<MemberProfile> getRecipientMembers() {
        return recipientMembers;
    }

    public void setRecipientMembers(List<MemberProfile> recipientMembers) {
        this.recipientMembers = recipientMembers;
    }

    public @NotBlank Boolean getPubliclyVisible() {
        return publiclyVisible;
    }

     public void setPubliclyVisible(Boolean publiclyVisible) {
        this.publiclyVisible = publiclyVisible;
    }
}
