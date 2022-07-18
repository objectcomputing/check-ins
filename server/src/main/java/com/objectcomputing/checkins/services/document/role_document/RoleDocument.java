package com.objectcomputing.checkins.services.document.role_document;

import io.micronaut.data.annotation.EmbeddedId;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@MappedEntity
@Table(name = "role_documents")
public class RoleDocument {

    @EmbeddedId
    private final RoleDocumentId roleDocumentId;

    @Column(name = "documentnumber")
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "the order in which the document appears")
    private int documentNumber;

    public RoleDocument(RoleDocumentId roleDocumentId, int documentNumber) {
        this.roleDocumentId = roleDocumentId;
        this.documentNumber = documentNumber;
    }

    public RoleDocument(UUID roleId, UUID documentId, int documentNumber) {
        this.roleDocumentId = new RoleDocumentId(roleId, documentId);
        this.documentNumber = documentNumber;
    }

    public RoleDocumentId getRoleDocumentId() {
        return roleDocumentId;
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
        RoleDocument that = (RoleDocument) o;
        return documentNumber == that.documentNumber && Objects.equals(roleDocumentId, that.roleDocumentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleDocumentId, documentNumber);
    }
}
