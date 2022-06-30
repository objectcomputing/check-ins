package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.document.Document;
import com.objectcomputing.checkins.services.document.role_document.RoleDocument;
import com.objectcomputing.checkins.services.role.Role;

public interface DocumentFixture extends RepositoryFixture {

    default Document createDefaultDocument() {
        return new Document("Sample Document", "Contains information", "/pdf/sample-document.pdf");
    }

    default Document saveDefaultDocument() {
        return getDocumentRepository().save(createDefaultDocument());
    }

    default RoleDocument createDefaultRoleDocument(Role role) {
        Document document = saveDefaultDocument();
        return new RoleDocument(role.getId(), document.getId(), 1);
    }

    default RoleDocument saveDefaultRoleDocument(Role role) {
        return getRoleDocumentRepository().save(createDefaultRoleDocument(role));
    }
}
