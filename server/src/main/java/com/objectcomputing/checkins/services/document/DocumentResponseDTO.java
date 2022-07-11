package com.objectcomputing.checkins.services.document;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

/**
 * Combines information from {@link Document} with a document number for sorting
 */
@Introspected
public class DocumentResponseDTO {

    @NotNull
    @Schema(description = "id of the document", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "the name of the document", required = true)
    private String name;

    @Nullable
    @Schema(description = "description of the document")
    private String description;

    @NotNull
    @Schema(description = "the URL where the document is accessed")
    private String url;

    @NotNull
    @Schema(description = "the order of the document")
    private int documentNumber;

    public DocumentResponseDTO(UUID id, String name, @Nullable String description, String url, int documentNumber) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.documentNumber = documentNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(int documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentResponseDTO that = (DocumentResponseDTO) o;
        return documentNumber == that.documentNumber && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url, documentNumber);
    }

    @Override
    public String toString() {
        return "DocumentResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", documentNumber=" + documentNumber +
                '}';
    }
}
