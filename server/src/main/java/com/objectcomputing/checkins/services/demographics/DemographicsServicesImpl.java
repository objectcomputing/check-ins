package com.objectcomputing.checkins.services.demographics;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.core.annotation.Nullable;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class DemographicsServicesImpl implements DemographicsServices{
    private final DemographicsRepository demographicsRepository;
    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final CurrentUserServices currentUserServices;

    public DemographicsServicesImpl(DemographicsRepository demographicsRepository, MemberProfileRetrievalServices memberProfileRetrievalServices, CurrentUserServices currentUserServices) {
        this.demographicsRepository = demographicsRepository;
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Demographics getById(UUID id) {
        Demographics demographics = demographicsRepository.findById(id).orElse(null);

        if (!currentUserServices.isAdmin() &&
                (demographics!= null && demographics.getMemberId() != null &&
                        !demographics.getMemberId().equals(currentUserServices.getCurrentUser().getId()))) {
            throw new PermissionException("You are not authorized to access this Demographics");
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
            throw new PermissionException("Requires admin privileges");
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
            throw new PermissionException("You are not authorized to update this demographic");
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
            throw new PermissionException("You are not authorized to create this Demographic");
        }

        Demographics demographicsRet;

        if (demographics.getMemberId() == null) {
            throw new BadArgException("Invalid member id %s", demographics.getId());
        } else if (memberProfileRetrievalServices.getById(demographics.getMemberId()).isEmpty()) {
            throw new BadArgException("Member Profile %s doesn't exist", demographics.getMemberId());
        } else if (demographics.getId() != null) {
            throw new AlreadyExistsException("Demographics %s already exists", demographics.getId());
        }

        demographicsRet = demographicsRepository.save(demographics);


        return demographicsRet;
    }

    @Override
    public List<Demographics> findAll() {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("Requires admin privileges");
        }

        return demographicsRepository.findAll();
    }

    @Override
    public Boolean deleteDemographics(@NotNull UUID id) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("Requires admin privileges");
        }

        if (demographicsRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Demographic id %s was not found", id);
        }

        demographicsRepository.deleteById(id);
        return true;
    }
}
