package com.objectcomputing.checkins.services.tags;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

@Singleton
public class TagServicesImpl implements TagServices {

    private final TagRepository tagRepository;
    private final CurrentUserServices currentUserServices;
    private final PermissionsValidation permissionsValidation;

    public TagServicesImpl(TagRepository tagRepository, CurrentUserServices currentUserServices, PermissionsValidation permissionsValidation) {
        this.tagRepository = tagRepository;
        this.currentUserServices = currentUserServices;
        this.permissionsValidation = permissionsValidation;
    }

    public Tag save(Tag tag) {

        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin);

        Tag tagToReturn = null;
        if (tag != null) {
            if (tag.getId() != null) {
                    throw new BadArgException(String.format("Found unexpected id %s for tag", tag.getId()));
            } else if (!tagRepository.findByNameIlike(tag.getName()).isEmpty()) {
                    throw new AlreadyExistsException(String.format("A tag named %s already exists.", tag.getName()));
            }

                tagToReturn = tagRepository.save(tag);
        }
            return tagToReturn;

    }

    public Tag read(@NotNull UUID uuid) {
        return tagRepository.findById(uuid).orElse(null);
    }

    public Set<Tag> findByFields(String name) {
        return tagRepository.search(name);
    }

    public Tag update(@NotNull Tag tag) {

        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin);

        Tag newTag = null;

        if (tag.getId() != null && tagRepository.findById(tag.getId()).isPresent()) {
            newTag = tagRepository.update(tag);
        } else {
            throw new BadArgException(String.format("tag %s does not exist, cannot update", tag.getId()));
        }

        return newTag;

    }

    public void delete(@NotNull UUID id) {

        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin);

        tagRepository.deleteById(id);
    }

}
