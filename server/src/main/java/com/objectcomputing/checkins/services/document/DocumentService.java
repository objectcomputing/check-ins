package com.objectcomputing.checkins.services.document;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.SequencedSet;
import java.util.UUID;

interface DocumentService {

    DocumentResponseDTO create(Document document);

    DocumentResponseDTO update(Document document);

    void deleteDocument(UUID documentId);

    /**
     * List all documents or all documents associated with a role.
     * If roleId is null, all documents are listed (in alphabetical order).
     * If roleId is not null, only documents associated with the role are listed (in the order they are associated).
     *
     * @param roleId the optional id of the role to list documents for, or null to list all documents
     * @return the list of documents in the order described above
     */
    List<DocumentResponseDTO> listDocuments(@Nullable UUID roleId);

    /**
     * Save a list of documents to a role.
     * The order of the documents in the list will determine the display_order field in the role_documentation table.
     * All existing documents for the role will be deleted and replaced with the new list.
     * Unknown document ids are ignored.
     *
     * @param roleId the id of the role to save the documents to
     * @param documentIds the list of document ids to save
     * @return the list of documents saved to the role
     */
    List<DocumentResponseDTO> saveDocumentsToRoles(UUID roleId, SequencedSet<UUID> documentIds);
}
