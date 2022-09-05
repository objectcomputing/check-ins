package com.objectcomputing.checkins.services.tags;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;


@Singleton
public class TagServicesImpl implements TagServices {

    private final TagRepository tagRepository;
    private final CurrentUserServices currentUserServices;

    public TagServicesImpl(TagRepository tagRepository, CurrentUserServices currentUserServices) {
        this.tagRepository = tagRepository;
        this.currentUserServices = currentUserServices;
    }

    public Tag save(Tag tag) {

        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        Tag tagToReturn = null;
        if (tag != null) {
            validate(tag.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id %s for tag", tag.getId());
            });
            validate(tagRepository.findByNameIlike(tag.getName()).isEmpty()).orElseThrow(() -> {
                throw new AlreadyExistsException("A tag named %s already exists.", tag.getName());
            });

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
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        validate(tag.getId() != null && tagRepository.findById(tag.getId()).isPresent()).orElseThrow(() -> {
            throw new BadArgException("tag %s does not exist, cannot update", tag.getId());
        });

        return tagRepository.update(tag);
    }

    public void delete(@NotNull UUID id) {

        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        tagRepository.deleteById(id);
    }

}
