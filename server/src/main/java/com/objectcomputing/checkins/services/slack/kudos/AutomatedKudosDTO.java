package com.objectcomputing.checkins.services.slack.kudos;

import io.micronaut.core.annotation.Introspected;

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
public class AutomatedKudosDTO {

    @NotBlank
    private String message;

    @NotNull
    private String externalId;

    @NotNull
    private UUID senderId;

    @NotNull
    private List<UUID> recipientIds;
}
