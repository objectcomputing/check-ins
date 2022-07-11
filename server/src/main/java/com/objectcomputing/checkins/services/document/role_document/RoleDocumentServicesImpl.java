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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    public RoleDocument save(RoleDocument roleDocument) {
        UUID roleId = roleDocument.getRoleDocumentId().getRoleId();
        UUID documentId = roleDocument.getRoleDocumentId().getDocumentId();

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

        if (roleDocumentRepository.findById(roleDocument.getRoleDocumentId()).isPresent()) {
            throw new AlreadyExistsException(String.format("There already exists a role document with %s", roleDocument.getRoleDocumentId()));
        }

        if (roleDocument.getDocumentNumber() < 1) {
            throw new BadArgException("Document number must be at least 1");
        }

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
    public RoleDocument update(RoleDocument roleDocument) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You are not allowed to do this operation");
        } else if (roleDocumentRepository.findById(roleDocument.getRoleDocumentId()).isEmpty()) {
            throw new BadArgException(String.format("Cannot update role document with nonexistent id %s", roleDocument.getRoleDocumentId()));
        } else if (roleDocument.getDocumentNumber() < 1) {
            throw new BadArgException("Document number must be at least 1");
        }

        return roleDocumentRepository.update(roleDocument);
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
        if (!userHasAccess) {
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
