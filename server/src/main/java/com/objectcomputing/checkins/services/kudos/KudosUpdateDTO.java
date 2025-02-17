package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Introspected
public class KudosUpdateDTO {
    @NotNull
    private UUID id;

    @NotBlank
    private String message;

    @Nullable
    private Boolean publiclyVisible;

    @NotNull
    private List<MemberProfile> recipientMembers;
}
