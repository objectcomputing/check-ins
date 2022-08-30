package com.objectcomputing.checkins.services.document.role_document;

import com.objectcomputing.checkins.services.document.DocumentResponseDTO;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface RoleDocumentServices {

    RoleDocument saveByIds(UUID roleId, UUID documentId);

    RoleDocument getById(@NotNull RoleDocumentId roleDocumentId);

    List<DocumentResponseDTO> update(RoleDocument roleDocument);

    void delete(@NotNull RoleDocumentId id);

    List<DocumentResponseDTO> getDocumentsByRole(UUID roleId);

    List<RoleDocumentResponseDTO> getAllDocuments();

}
