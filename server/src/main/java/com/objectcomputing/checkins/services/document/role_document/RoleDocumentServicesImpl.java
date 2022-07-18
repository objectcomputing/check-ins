package com.objectcomputing.checkins.services.document.role_document;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.document.Document;
import com.objectcomputing.checkins.services.document.DocumentResponseDTO;
import com.objectcomputing.checkins.services.document.DocumentServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Singleton
public class RoleDocumentServicesImpl implements RoleDocumentServices {

    private static final Logger LOG = LoggerFactory.getLogger(RoleDocumentServicesImpl.class);
    private final RoleDocumentRepository roleDocumentRepository;
    private final CurrentUserServices currentUserServices;
    private final RoleServices roleServices;
    private final DocumentServices documentServices;

    public RoleDocumentServicesImpl(RoleDocumentRepository roleDocumentRepository,
                                    CurrentUserServices currentUserServices,
                                    RoleServices roleServices,
                                    DocumentServices documentServices) {
        this.roleDocumentRepository = roleDocumentRepository;
        this.currentUserServices = currentUserServices;
        this.roleServices = roleServices;
        this.documentServices = documentServices;
    }

    @Override
    public RoleDocument saveByIds(UUID roleId, UUID documentId) {

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not allowed to do this operation");
        } else if (roleServices.read(roleId) == null) {
            throw new BadArgException(String.format("Cannot save role document with nonexistent role id %s", roleId));
        }

        try {
            documentServices.getById(documentId);
        } catch (NotFoundException e) {
            throw new BadArgException(String.format("Cannot save role document with nonexistent document id %s", documentId));
        }

        RoleDocumentId newRoleDocumentId = new RoleDocumentId(roleId, documentId);
        if (roleDocumentRepository.findById(newRoleDocumentId).isPresent()) {
            throw new AlreadyExistsException(String.format("There already exists a role document with %s", newRoleDocumentId));
        }

        // Set the order of the document to be last (one greater than the current highest)
        int documentNumber = 1;
        List<DocumentResponseDTO> relatedDocuments = roleDocumentRepository.findDocumentsByRoleId(roleId);
        if (!relatedDocuments.isEmpty()) {
            DocumentResponseDTO maxDocument = relatedDocuments.stream().max(Comparator.comparingInt(DocumentResponseDTO::getDocumentNumber)).get();
            documentNumber = maxDocument.getDocumentNumber() + 1;
        }

        RoleDocument roleDocument = new RoleDocument(roleId, documentId, documentNumber);
        return roleDocumentRepository.save(roleDocument);
    }

    @Override
    public RoleDocument getById(RoleDocumentId roleDocumentId) {
        Set<Role> userRoles = roleServices.findUserRoles(currentUserServices.getCurrentUser().getId());
        boolean userHasAccess = userRoles.stream().anyMatch(userRole -> userRole.getId().equals(roleDocumentId.getRoleId()));
        if (!userHasAccess) {
            throw new PermissionException(String.format("You are not allowed to access documents for role id %s", roleDocumentId.getRoleId()));
        }

        return roleDocumentRepository.findById(roleDocumentId).orElse(null);
    }

    @Override
    public List<DocumentResponseDTO> update(RoleDocument roleDocument) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not allowed to do this operation");
        } else if (roleDocumentRepository.findById(roleDocument.getRoleDocumentId()).isEmpty()) {
            throw new BadArgException(String.format("Cannot update role document with nonexistent id %s", roleDocument.getRoleDocumentId()));
        }

        // Get all documents for this role
        List<DocumentResponseDTO> relatedDocuments = roleDocumentRepository.findDocumentsByRoleId(roleDocument.getRoleDocumentId().getRoleId());
        DocumentResponseDTO reorderedItem = relatedDocuments.stream()
                .filter(doc -> doc.getId().equals(roleDocument.getRoleDocumentId().getDocumentId()))
                .findFirst()
                .orElseThrow(() -> {
                    throw new BadArgException("Cannot find document associated with this role");
                });

        // Move the updated role document to its new position
        relatedDocuments.remove(reorderedItem);
        relatedDocuments.add(roleDocument.getDocumentNumber() - 1, reorderedItem);

        // Update each document number based on its new index
        for (int i = 0; i < relatedDocuments.size(); i++) {
            relatedDocuments.get(i).setDocumentNumber(i + 1);
        }

        // Update all related role documents
        List<RoleDocument> updatedRoleDocuments = new ArrayList<>(relatedDocuments.size());
        relatedDocuments.forEach(doc -> {
            UUID roleId = roleDocument.getRoleDocumentId().getRoleId();
            UUID documentId = doc.getId();
            RoleDocument updatedRoleDocument = new RoleDocument(roleId, documentId, doc.getDocumentNumber());
            updatedRoleDocuments.add(updatedRoleDocument);
        });
        roleDocumentRepository.updateAll(updatedRoleDocuments);

        return relatedDocuments;
    }

    @Override
    public void delete(RoleDocumentId id) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not allowed to do this operation");
        } else if (roleDocumentRepository.findById(id).isEmpty()) {
            throw new BadArgException(String.format("Cannot delete role document with nonexistent id %s", id));
        }

        roleDocumentRepository.deleteById(id);
    }

    @Override
    public List<DocumentResponseDTO> getDocumentsByRole(UUID roleId) {
        if (roleServices.read(roleId) == null) {
            throw new BadArgException(String.format("Cannot get documents for nonexistent role id %s", roleId));
        }

        Set<Role> userRoles = roleServices.findUserRoles(currentUserServices.getCurrentUser().getId());
        boolean userHasAccess = userRoles.stream().anyMatch(userRole -> userRole.getId().equals(roleId));
        if (!currentUserServices.isAdmin() && !userHasAccess) {
            throw new PermissionException(String.format("You are not allowed to access documents for role id %s", roleId));
        }

        return roleDocumentRepository.findDocumentsByRoleId(roleId);
    }

    @Override
    public List<RoleDocumentResponseDTO> getAllDocuments() {
        List<Document> documents = documentServices.findAll();
        List<RoleDocumentResponseDTO> documentsWithRoles = new ArrayList<>(documents.size());

        documents.forEach(document -> {
            List<RoleDocument> roles = roleDocumentRepository.findRoleDocumentsByDocumentId(document.getId());
            RoleDocumentResponseDTO dto = new RoleDocumentResponseDTO(document.getId(), document.getName(), document.getDescription(), document.getUrl(), roles);
            documentsWithRoles.add(dto);
        });

        return documentsWithRoles;
    }
}
