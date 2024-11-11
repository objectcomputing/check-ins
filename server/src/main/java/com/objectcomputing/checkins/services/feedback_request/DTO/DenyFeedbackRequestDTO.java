package com.objectcomputing.checkins.services.feedback_request.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DenyFeedbackRequestDTO {

    @NotBlank(message = "Reason cannot be blank")
    private String reason;

    @NotNull(message = "Denier cannot be null")
    @Valid
    private DenierDTO denier;

    @NotNull(message = "Creator cannot be null")
    @Valid
    private CreatorDTO creator;

    // Constructors
    public DenyFeedbackRequestDTO() {}

    public DenyFeedbackRequestDTO(String reason, DenierDTO denier, CreatorDTO creator) {
        this.reason = reason;
        this.denier = denier;
        this.creator = creator;
    }

    // Getters
    public String getReason() {
        return reason;
    }

    public DenierDTO getDenier() {
        return denier;
    }

    public CreatorDTO getCreator() {
        return creator;
    }

    // Setters
    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setDenier(DenierDTO denier) {
        this.denier = denier;
    }

    public void setCreator(CreatorDTO creator) {
        this.creator = creator;
    }
}
