package com.objectcomputing.checkins.services.document;

import java.util.List;
import java.util.UUID;

public interface DocumentServices {

    Document save(Document document);

    Document getById(UUID id);

    Document update(Document document);

    void delete(UUID id);

    List<Document> findAll();

}
