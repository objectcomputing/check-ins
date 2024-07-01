package com.objectcomputing.checkins.services.document;

import io.micronaut.serde.annotation.Serdeable;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Serdeable
class DocumentCreateDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String url;

    @Nullable
    private String description;

    Document toDocument(UUID id) {
        return new Document(id, name, url, description);
    }

    Document toDocument() {
        return new Document(name, url, description);
    }
}
