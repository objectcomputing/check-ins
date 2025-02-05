package com.objectcomputing.checkins.services.feedback_external_recipient;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Introspected
public class FeedbackExternalRecipientCreateDTO {

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

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackExternalRecipientCreateDTO that = (FeedbackExternalRecipientCreateDTO) o;
        return
                Objects.equals(email, that.email)
                && Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(companyName, that.companyName)
                && Objects.equals(inactive, that.inactive)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, lastName, companyName, inactive);
    }

}

