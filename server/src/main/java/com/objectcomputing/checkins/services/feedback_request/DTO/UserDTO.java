package com.objectcomputing.checkins.services.feedback_request.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

import io.micronaut.core.annotation.Introspected;


@Introspected

public class UserDTO {

    @NotNull(message = "User ID cannot be null")
    private UUID id;

    @NotBlank(message = "User name cannot be blank")
    private String name;

    // Constructors
    public UserDTO() {}

    // Constructor with only ID (for cases where only ID is required)
    public UserDTO(UUID id) {
        this.id = id;
    }

    // Constructor with ID and name (for cases where both ID and name are required)
    public UserDTO(UUID id, String name) {
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
