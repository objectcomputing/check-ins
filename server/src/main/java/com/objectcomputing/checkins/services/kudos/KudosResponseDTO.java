package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.team.Team;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
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

    KudosResponseDTO(UUID id, String message, UUID senderId, LocalDate dateCreated, LocalDate dateApproved, Boolean publiclyVisible) {
        this(id, message, senderId, dateCreated, dateApproved, publiclyVisible, null, Collections.emptyList());
    }
}
