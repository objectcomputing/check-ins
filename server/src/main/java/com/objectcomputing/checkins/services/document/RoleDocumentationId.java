package com.objectcomputing.checkins.services.document;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Embeddable
class RoleDocumentationId implements Serializable {

    @NotNull
    @TypeDef(type = DataType.STRING)
    @Column(name = "role_id")
    private UUID roleId;

    @NotNull
    @TypeDef(type = DataType.STRING)
    @Column(name = "document_id")
    private UUID documentId;

    public RoleDocumentationId() {
    }

    RoleDocumentationId(UUID roleId, UUID documentId) {
        this.roleId = roleId;
        this.documentId = documentId;
    }
}
