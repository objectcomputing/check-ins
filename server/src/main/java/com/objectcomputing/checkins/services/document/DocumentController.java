package com.objectcomputing.checkins.services.document;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Controller("/services/document")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "documents")
class DocumentController {

    private final DocumentService documentService;

    DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * List all documents.
     *
     * @return list of {@link DocumentResponseDTO}
     */
    @Get
    List<DocumentResponseDTO> listDocuments() {
        return documentService.listDocuments(null);
    }

    /**
     * List all documents for a given role.
     *
     * @param roleId the id of the role to filter by
     * @return list of {@link DocumentResponseDTO}
     */
    @Get("/{roleId}")
    List<DocumentResponseDTO> listDocumentsForRole(UUID roleId) {
        return documentService.listDocuments(roleId);
    }

    /**
     * Create a new document.
     *
     * @param document the document to create
     * @return the created document
     */
    @Post
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    DocumentResponseDTO createDocument(@Body @Valid DocumentCreateDTO document) {
        return documentService.create(document.toDocument());
    }

    /**
     * Update a document.
     *
     * @param documentId the id of the document to update
     * @param document   the updated document
     * @return the updated document
     */
    @Put("/{documentId}")
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    DocumentResponseDTO updateDocument(UUID documentId, @Body @Valid DocumentCreateDTO document) {
        return documentService.update(document.toDocument(documentId));
    }

    /**
     * Delete a document.
     * Requires the document to have been removed from all Roles.
     * @param documentId the id of the document to delete
     */
    @Delete("/{documentId}")
    @Status(HttpStatus.NO_CONTENT)
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    void deleteDocument(UUID documentId) {
        documentService.deleteDocument(documentId);
    }

    /**
     * Replaces all the documents for a given role in the order specified in the body.
     * Requires the role to exist.
     * Duplicate document ids will be ignored.
     *
     * @param roleId      The ID of the role to save the documents for
     * @param documentIds The IDs of the documents to save in the order they should be displayed
     * @return The list of documents saved to the role
     */
    @Post("/{roleId}")
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    List<DocumentResponseDTO> saveDocumentsToRoles(UUID roleId, @Body List<UUID> documentIds) {
        return documentService.saveDocumentsToRoles(roleId, new LinkedHashSet<>(documentIds));
    }
}
