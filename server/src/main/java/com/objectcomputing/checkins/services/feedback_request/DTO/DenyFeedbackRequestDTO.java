package com.objectcomputing.checkins.services.feedback_request.DTO;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Introspected

public class DenyFeedbackRequestDTO {

    @NotBlank(message = "Reason cannot be blank")
    private String reason;

    @NotNull(message = "Denier cannot be null")
    @Valid
    private UserDTO denier;

    @NotNull(message = "Creator cannot be null")
    @Valid
    private UserDTO creator;

    // Constructors
    public DenyFeedbackRequestDTO() {}

    public DenyFeedbackRequestDTO(String reason, UserDTO denier, UserDTO creator) {
        this.reason = reason;
        this.denier = denier;
        this.creator = creator;
    }

    // Getters
    public String getReason() {
        return reason;
    }

    public UserDTO getDenier() {
        return denier;
    }

    public UserDTO getCreator() {
        return creator;
    }

    // Setters
    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDenier(UserDTO denier) {
        this.denier = denier;
    }

    public void setCreator(UserDTO creator) {
        this.creator = creator;
    }
}
