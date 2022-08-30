package com.objectcomputing.checkins.services.document.role_document;

import io.micronaut.data.annotation.Embeddable;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RoleDocumentId {

    @Column(name = "roleid")
    @TypeDef(type = DataType.STRING)
    private final UUID roleId;

    @Column(name = "documentid")
    @TypeDef(type = DataType.STRING)
    private final UUID documentId;

    public RoleDocumentId(UUID roleId, UUID documentId) {
        this.roleId = roleId;
        this.documentId = documentId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleDocumentId that = (RoleDocumentId) o;
        return Objects.equals(roleId, that.roleId) && Objects.equals(documentId, that.documentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId, documentId);
    }

    @Override
    public String toString() {
        return String.format("{roleId=%s, documentId=%s}", roleId, documentId);
    }
}
