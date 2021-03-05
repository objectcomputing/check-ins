package com.objectcomputing.checkins.services.guild;

import com.objectcomputing.checkins.services.validate.crud.CRUDValidator;
import com.objectcomputing.checkins.services.validate.crud.GuildCRUDValidator;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class GuildServicesImpl implements GuildServices {

    private final GuildRepository guildsRepo;
    private final CRUDValidator<Guild> crudValidator;

    public GuildServicesImpl(GuildRepository guildsRepo, GuildCRUDValidator crudValidator) {
        this.guildsRepo = guildsRepo;
        this.crudValidator = crudValidator;
    }

    public Guild save(@NotNull Guild guild) {
        crudValidator.validatePermissionsCreate(guild);
        crudValidator.validateArgumentsCreate(guild);
        return guildsRepo.save(guild);
    }

    public Guild read(@NotNull UUID guildId) {
        return guildsRepo.findById(guildId).orElse(null);
    }

    public Guild update(@NotNull Guild guild) {
        crudValidator.validatePermissionsUpdate(guild);
        crudValidator.validateArgumentsUpdate(guild);
        return guildsRepo.update(guild);
    }

    public Set<Guild> findByFields(String name, UUID memberid) {
        String likeName = null;
        if (name != null) {
            likeName = "%" + name + "%";
        }

        return new HashSet<>(guildsRepo.search(likeName, nullSafeUUIDToString(memberid)));
    }

    public Boolean delete(@NotNull UUID id) {
        Guild guild = guildsRepo.findById(id).orElse(null);
        crudValidator.validatePermissionsDelete(guild);
        crudValidator.validateArgumentsDelete(guild);

        guildsRepo.deleteById(id);
        return true;
    }
}
