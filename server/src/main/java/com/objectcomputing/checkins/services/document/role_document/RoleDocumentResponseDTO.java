package com.objectcomputing.checkins.services.document.role_document;

import com.objectcomputing.checkins.services.document.Document;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Combines {@link Document} information with list of associated {@link RoleDocument} objects
 */
@Introspected
public class RoleDocumentResponseDTO {

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
    @Schema(description = "the list of roles that can access this document")
    private List<RoleDocument> roles;

    public RoleDocumentResponseDTO(UUID id, String name, @Nullable String description, String url, List<RoleDocument> roles) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.roles = roles;
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

    public List<RoleDocument> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDocument> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDocumentResponseDTO that = (RoleDocumentResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(url, that.url) && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, url, roles);
    }

    @Override
    public String toString() {
        return "RoleDocumentResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", roles=" + roles +
                '}';
    }
}
