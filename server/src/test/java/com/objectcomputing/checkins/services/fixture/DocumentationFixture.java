package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.document.Document;

public interface DocumentationFixture extends RepositoryFixture {

    default Document createDocument(String name, String url) {
        return createDocument(name, url, null);
    }

    default Document createDocument(String name, String url, String description) {
        return getDocumentRepository().save(new Document(name, url, description));
    }
}
