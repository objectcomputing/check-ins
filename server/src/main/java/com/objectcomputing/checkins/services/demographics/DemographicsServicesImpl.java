package com.objectcomputing.checkins.services.demographics;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;
import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class DemographicsServicesImpl implements DemographicsServices{

    private final DemographicsRepository demographicsRepository;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;

    public DemographicsServicesImpl(DemographicsRepository demographicsRepository, MemberProfileServices memberProfileServices, CurrentUserServices currentUserServices) {
        this.demographicsRepository = demographicsRepository;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Demographics getById(UUID id) {
        Demographics demographics = demographicsRepository.findById(id).orElse(null);

        if (!currentUserServices.isAdmin() &&
                (demographics!= null && demographics.getMemberId() != null &&
                        !demographics.getMemberId().equals(currentUserServices.getCurrentUser().getId()))) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return demographics;
    }

    @Override
    public List<Demographics> findByValues(@Nullable UUID memberId,
                                          @Nullable String gender,
                                          @Nullable String degreeLevel,
                                          @Nullable Integer industryTenure,
                                          @Nullable Boolean personOfColor,
                                          @Nullable Boolean veteran,
                                          @Nullable Integer militaryTenure,
                                          @Nullable String militaryBranch) {

        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return new ArrayList<>(demographicsRepository.searchByValues(nullSafeUUIDToString(memberId),
                gender,
                degreeLevel,
                industryTenure,
                personOfColor,
                veteran,
                militaryTenure,
                militaryBranch));
    }

    @Override
    public Demographics updateDemographics(Demographics demographics) {
        if (!currentUserServices.isAdmin() &&
                (demographics.getMemberId() != null &&
                        !demographics.getMemberId().equals(currentUserServices.getCurrentUser().getId()))) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        Demographics newDemographics;
        if (demographics.getId() != null && demographicsRepository.findById(demographics.getId()).isPresent()) {
            newDemographics = demographicsRepository.update(demographics);
        } else {
            throw new BadArgException((String.format("Demographics %s does not exist, cannot update", demographics.getId())));
        }

        return newDemographics;
    }

    @Override
    public Demographics saveDemographics(@NotNull Demographics demographics) {
        if (!currentUserServices.isAdmin() &&
                (demographics.getMemberId() != null &&
                        !demographics.getMemberId().equals(currentUserServices.getCurrentUser().getId()))) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        Demographics demographicsRet;

        if (demographics.getMemberId() == null) {
            throw new BadArgException(String.format("Invalid member id %s", demographics.getId()));
        } else if (memberProfileServices.getById(demographics.getMemberId()) == null) {
            throw new BadArgException(String.format("Member Profile %s doesn't exist", demographics.getMemberId()));
        } else if (demographics.getId() != null) {
            throw new AlreadyExistsException(String.format("Demographics %s already exists", demographics.getId()));
        }

        demographicsRet = demographicsRepository.save(demographics);


        return demographicsRet;
    }

    @Override
    public List<Demographics> findAll() {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return demographicsRepository.findAll();
    }

    @Override
    public void deleteDemographics(@NotNull UUID id) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (demographicsRepository.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("Demographic id %s was not found", id));
        }

        demographicsRepository.deleteById(id);
    }
}
