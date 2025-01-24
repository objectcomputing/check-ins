package com.objectcomputing.checkins.services.document;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
class DocumentServiceImpl implements DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final String DOCUMENT_NAME_ALREADY_EXISTS = "Document named '%s' already exists";
    private static final String DOCUMENT_URL_ALREADY_EXISTS = "Document with a URL '%s' already exists";

    private final DocumentRepository documentRepo;
    private final RoleDocumentationRepository roleDocumentationRepo;
    private final RoleRepository roleRepo;

    DocumentServiceImpl(
            DocumentRepository documentRepo,
            RoleDocumentationRepository roleDocumentationRepo,
            RoleRepository roleRepo
    ) {
        this.documentRepo = documentRepo;
        this.roleDocumentationRepo = roleDocumentationRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    public DocumentResponseDTO create(Document document) {
        if (document.getId() != null) {
            return update(document);
        }
        Optional<Document> documentByName = documentRepo.findByName(document.getName());
        Optional<Document> documentByUrl = documentRepo.findByUrl(document.getUrl());
        if (documentByName.isPresent()) {
            throw new BadArgException(DOCUMENT_NAME_ALREADY_EXISTS.formatted(document.getName()));
        }
        if (documentByUrl.isPresent()) {
            throw new BadArgException(DOCUMENT_URL_ALREADY_EXISTS.formatted(document.getUrl()));
        }
        return documentRepo.save(document).asResponseDTO();
    }

    @Override
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    public DocumentResponseDTO update(Document document) {
        Optional<Document> documentByName = documentRepo.findByName(document.getName());
        Optional<Document> documentByUrl = documentRepo.findByUrl(document.getUrl());
        if (documentByName.map(d -> !d.getId().equals(document.getId())).orElse(false)) {
            throw new BadArgException(DOCUMENT_NAME_ALREADY_EXISTS.formatted(document.getName()));
        }
        if (documentByUrl.map(d -> !d.getId().equals(document.getId())).orElse(false)) {
            throw new BadArgException(DOCUMENT_URL_ALREADY_EXISTS.formatted(document.getUrl()));
        }
        return documentRepo.update(document).asResponseDTO();
    }

    @Override
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    public void deleteDocument(UUID documentId) {
        if (roleDocumentationRepo.documentIsStillReferenced(documentId)) {
            throw new BadArgException("Document is still referenced by a role");
        }
        documentRepo.deleteById(documentId);
    }

    @Override
    public List<DocumentResponseDTO> listDocuments(@Nullable UUID roleId) {
        if (roleId == null) {
            return documentRepo.findAllOrderByNameAndUrl().stream()
                    .map(Document::asResponseDTO)
                    .toList();
        } else {
            return documentRepo.findByRoleId(roleId).stream()
                    .map(Document::asResponseDTO)
                    .toList();
        }
    }

    @Override
    @Transactional
    @RequiredPermission(Permission.CAN_ADMINISTER_DOCUMENTATION)
    public List<DocumentResponseDTO> saveDocumentsToRoles(UUID roleId, SequencedSet<UUID> documentIds) {
        // Check the Role exists
        if (!roleRepo.existsById(roleId)) {
            throw new BadArgException("Role does not exist: " + roleId);
        }
        // Clear out the existing role documentation for the role
        roleDocumentationRepo.deleteByRoleId(roleId);

        // Save the new role documentation with the display order
        final AtomicInteger displayOrder = new AtomicInteger(1);
        documentIds.forEach(documentId -> {
            if (!documentRepo.existsById(documentId)) {
                LOG.warn("Document {} does not exist to assign to role {}", documentId, roleId);
                return;
            }
            roleDocumentationRepo.save(new RoleDocumentation(roleId, documentId, displayOrder.getAndIncrement()));
        });

        return listDocuments(roleId);
    }
}
