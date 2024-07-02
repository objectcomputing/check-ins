package com.objectcomputing.checkins.services.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@IdClass(RoleDocumentationId.class)
@Table(name = "role_documentation")
class RoleDocumentation {

    @EmbeddedId
    @Schema(description = "id of the document and role relationship")
    private RoleDocumentationId id;

    @Column(name = "display_order")
    @Schema(description = "the order the document should be displayed in (ascending)")
    private int displayOrder;

    public RoleDocumentation() {
    }

    RoleDocumentation(UUID roleId, UUID documentId, int displayOrder) {
        this.id = new RoleDocumentationId(roleId, documentId);
        this.displayOrder = displayOrder;
    }
}
