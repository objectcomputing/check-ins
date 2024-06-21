package com.objectcomputing.checkins.services.document;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@Serdeable
class DocumentResponseDTO {

    @NotNull
    private final UUID id;

    @NotNull
    private final String name;

    @NotNull
    private final String url;

    @Nullable
    private final String description;

    DocumentResponseDTO(UUID id, String name, String url, @Nullable String description) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.description = description;
    }
}
