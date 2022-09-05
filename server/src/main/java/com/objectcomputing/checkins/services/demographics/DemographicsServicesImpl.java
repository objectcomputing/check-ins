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
import static com.objectcomputing.checkins.util.Validation.validate;

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
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        validate(currentUserServices.isAdmin() ||
                (demographics != null && demographics.getMemberId() != null && demographics.getMemberId().equals(currentUserId))).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to access this Demographics");
        });

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

        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("Requires admin privileges");
        });

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

        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        validate(currentUserServices.isAdmin()
                || (demographics.getMemberId() != null && demographics.getMemberId().equals(currentUserId))).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to update this demographic");
        });

        validate(demographics.getId() != null && demographicsRepository.findById(demographics.getId()).isPresent()).orElseThrow(() -> {
            throw new BadArgException("Demographics %s does not exist, cannot update", demographics.getId());
        });

        return demographicsRepository.update(demographics);
    }

    @Override
    public Demographics saveDemographics(@NotNull Demographics demographics) {

        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        validate(currentUserServices.isAdmin()
                || demographics.getMemberId().equals(currentUserId)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to create this Demographic");
        });

        validate(demographics.getMemberId() != null).orElseThrow(() -> {
            throw new BadArgException("Invalid member id %s", demographics.getId());
        });
        validate(memberProfileRetrievalServices.existsById(demographics.getMemberId())).orElseThrow(() -> {
            throw new BadArgException("Member Profile %s doesn't exist", demographics.getMemberId());
        });
        validate(demographics.getId() == null).orElseThrow(() -> {
            throw new AlreadyExistsException("Demographics %s already exists", demographics.getId());
        });

        return demographicsRepository.save(demographics);
    }

    @Override
    public List<Demographics> findAll() {
        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("Requires admin privileges");
        });

        return demographicsRepository.findAll();
    }

    @Override
    public Boolean deleteDemographics(@NotNull UUID id) {

        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("Requires admin privileges");
        });
        validate(demographicsRepository.findById(id).isPresent()).orElseThrow(() -> {
            throw new NotFoundException("Demographic id %s was not found", id);
        });

        demographicsRepository.deleteById(id);
        return true;
    }
}
