package com.objectcomputing.checkins.services.feedback_request.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class DenierDTO {

    @NotNull(message = "Denier ID cannot be null")
    private UUID id;

    @NotBlank(message = "Denier name cannot be blank")
    private String name;

    // Constructors
    public DenierDTO() {}

    public DenierDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
