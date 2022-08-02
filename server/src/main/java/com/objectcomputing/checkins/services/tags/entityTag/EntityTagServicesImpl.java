package com.objectcomputing.checkins.services.tags.entityTag;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.tags.TagRepository;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTag.EntityType;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class EntityTagServicesImpl implements EntityTagServices {

    private final EntityTagRepository entityTagRepository;
    private final TagRepository tagRepository;
    private final CurrentUserServices currentUserServices;

    public EntityTagServicesImpl(EntityTagRepository entityTagRepository,
                                 TagRepository tagRepository,
                                 CurrentUserServices currentUserServices) {
        this.entityTagRepository = entityTagRepository;
        this.tagRepository = tagRepository;
        this.currentUserServices = currentUserServices;
    }

    public EntityTag save(EntityTag entityTag) {

        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        EntityTag entityTagToReturn = null;
        if (entityTag != null) {
            final UUID entityId = entityTag.getEntityId();
            final UUID tagId = entityTag.getTagId();

            validate(entityId != null && tagId != null).orElseThrow(() -> {
                throw new BadArgException("Invalid entity tag %s", entityTag);
            });
            validate(entityTag.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id %s for entity tag", entityTag.getId());
            });
            tagRepository.findById(tagId).orElseThrow(() -> {
                throw new BadArgException("Tag %s doesn't exist", tagId);
            });

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
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        validate(entityTag.getId() != null && entityTagRepository.findById(entityTag.getId()).isPresent()).orElseThrow(() -> {
            throw new BadArgException("Entity tag %s does not exist, cannot update", entityTag.getId());
        });

        return entityTagRepository.update(entityTag);
    }

    public void delete(@NotNull UUID id) {

        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        entityTagRepository.deleteById(id);
    }

}
