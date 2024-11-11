package com.objectcomputing.checkins.services.feedback_request.DTO;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreatorDTO {

    @NotNull(message = "Creator ID cannot be null")
    private UUID id;

    // Constructors
    public CreatorDTO() {}

    public CreatorDTO(UUID id) {
        this.id = id;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }
}
