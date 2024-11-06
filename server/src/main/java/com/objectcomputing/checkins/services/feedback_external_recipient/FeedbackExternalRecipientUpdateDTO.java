package com.objectcomputing.checkins.services.feedback_external_recipient;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class FeedbackExternalRecipientUpdateDTO {

    @NotNull
    @Schema(description = "id of the feedback-external-recipient")
    private UUID id;

    @NotBlank
    @Schema(description = "email of the feedback-external-recipient")
    private String email;

    @Nullable
    @Schema(description = "first name of the feedback-external-recipient")
    private String firstName;

    @Nullable
    @Schema(description = "last name of the feedback-external-recipient")
    private String lastName;

    @Nullable
    @Schema(description = "company-name of the feedback-external-recipient")
    private String companyName;

    @Nullable
    @Schema(description = "Indicates if the external-recipient can no longer be used for feedback requests")
    private Boolean inactive;

}

