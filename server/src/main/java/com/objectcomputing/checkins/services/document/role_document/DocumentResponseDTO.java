package com.objectcomputing.checkins.services.document.role_document;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Introspected
public class DocumentResponseDTO {

    @NotNull
    @Column(name = "id")
    @Schema(description = "id of the document", required = true)
    private UUID id;

    @NotNull
    @Column(name = "name")
    @Schema(description = "the name of the document", required = true)
    private String name;

    @Nullable
    @Column(name = "description")
    @Schema(description = "description of the document")
    private String description;

    @NotNull
    @Column(name = "url")
    @Schema(description = "the URL where the document is accessed")
    private String url;

    @NotNull
    @Column(name = "documentnumber")
    @Schema(description = "the order in which the document appears")
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
}
