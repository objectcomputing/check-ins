package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.tags.TagRepository;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;


@Singleton
public class EntityTagServicesImpl implements EntityTagServices {

    private final EntityTagRepository entityTagRepository;
    private final TagRepository tagRepository;
    private final CurrentUserServices currentUserServices;
    private final PermissionsValidation permissionsValidation;

    public EntityTagServicesImpl(EntityTagRepository entityTagRepository,
                                 TagRepository tagRepository,
                                 CurrentUserServices currentUserServices,
                                 PermissionsValidation permissionsValidation) {
        this.entityTagRepository = entityTagRepository;
        this.tagRepository = tagRepository;
        this.currentUserServices = currentUserServices;
        this.permissionsValidation = permissionsValidation;
    }

    public EntityTag save(EntityTag entityTag) {

        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");

        EntityTag entityTagToReturn = null;
        if (entityTag != null) {
            final UUID entityId = entityTag.getEntityId();
            final UUID tagId = entityTag.getTagId();
            if (entityId == null || tagId == null) {
                throw new BadArgException(String.format("Invalid entity tag %s", entityTag));
            } else if (entityTag.getId() != null) {
                throw new BadArgException(String.format("Found unexpected id %s for entity tag", entityTag.getId()));
            } else if (tagRepository.findById(tagId).isEmpty()) {
                throw new BadArgException(String.format("Tag %s doesn't exist", tagId));
            }

            entityTagToReturn = entityTagRepository.save(entityTag);
        }
        return entityTagToReturn;

    }

    public EntityTag read(@NotNull UUID id) {
        return entityTagRepository.findById(id).orElse(null);
    }

    public Set<EntityTag> findByFields(UUID entityId, UUID tagId, EntityType type) {
        return entityTagRepository.search(nullSafeUUIDToString(entityId), nullSafeUUIDToString(tagId), type);
    }

    public EntityTag update(@NotNull EntityTag entityTag) {

        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");

        EntityTag newEntityTag = null;

        if (entityTag.getId() != null && entityTagRepository.findById(entityTag.getId()).isPresent()) {
            newEntityTag = entityTagRepository.update(entityTag);
        } else {
            throw new BadArgException(String.format("Entity tag %s does not exist, cannot update", entityTag.getId()));
        }

        return newEntityTag;

    }


    public void delete(@NotNull UUID id) {

        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");

        entityTagRepository.deleteById(id);
    }


}
