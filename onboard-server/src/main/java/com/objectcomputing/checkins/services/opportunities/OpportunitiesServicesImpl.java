package com.objectcomputing.checkins.services.opportunities;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.validate.PermissionsValidation;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;
import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class OpportunitiesServicesImpl implements OpportunitiesService {

    private final OpportunitiesRepository opportunitiesResponseRepo;
    private final MemberProfileRepository memberRepo;
    private final PermissionsValidation permissionsValidation;
    private final CurrentUserServices currentUserServices;

    public OpportunitiesServicesImpl(OpportunitiesRepository opportunitiesResponseRepo,
                              MemberProfileRepository memberRepo,
                              PermissionsValidation permissionsValidation,
                              CurrentUserServices currentUserServices) {
        this.opportunitiesResponseRepo = opportunitiesResponseRepo;
        this.memberRepo = memberRepo;
        this.permissionsValidation = permissionsValidation;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Opportunities save(Opportunities opportunitiesResponse) {
        Opportunities opportunitiesResponseRet = null;
        if(opportunitiesResponse!=null){
            MemberProfile member = currentUserServices.getCurrentUser();
            if(member!=null) {
                opportunitiesResponse.setSubmittedBy(member.getId());
                opportunitiesResponse.setSubmittedOn(LocalDate.now());
            }
            if(opportunitiesResponse.getId()!=null){
                throw new BadArgException(String.format("Found unexpected id for opportunities %s", opportunitiesResponse.getId()));
            }
            opportunitiesResponseRet = opportunitiesResponseRepo.save(opportunitiesResponse);
        }
        return opportunitiesResponseRet ;
    }

    @Override
    public Opportunities update(Opportunities opportunitiesResponse) {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        Opportunities opportunitiesResponseRet = null;
        if(opportunitiesResponse!=null){
            final UUID id = opportunitiesResponse.getId();
            final UUID memberId = opportunitiesResponse.getSubmittedBy();
            LocalDate surSubDate = opportunitiesResponse.getSubmittedOn();
            if(id==null||opportunitiesResponseRepo.findById(id).isEmpty()){
                throw new BadArgException(String.format("Unable to find opportunities record with id %s", opportunitiesResponse.getId()));
            }else if(memberRepo.findById(memberId).isEmpty()){
                throw new BadArgException(String.format("Member %s doesn't exist", memberId));
            } else if(memberId==null) {
                throw new BadArgException(String.format("Invalid opportunities %s", opportunitiesResponse));
            } else if(surSubDate.isBefore(LocalDate.EPOCH) || surSubDate.isAfter(LocalDate.MAX)) {
                throw new BadArgException(String.format("Invalid date for opportunities submission date %s",memberId));
            }

            opportunitiesResponseRet = opportunitiesResponseRepo.update(opportunitiesResponse);
        }
        return opportunitiesResponseRet;
    }

    @Override
    public void delete(@NotNull UUID id) {
        final boolean isAdmin = currentUserServices.isAdmin();
        permissionsValidation.validatePermissions(!isAdmin, "User is unauthorized to do this operation");
        opportunitiesResponseRepo.deleteById(id);
    }

    @Override
    public ArrayList<Opportunities> findByFields(String name, String description,UUID submittedBy) {
        final ArrayList<Opportunities> opportunitiesResponse = new ArrayList<>(opportunitiesResponseRepo.searchByValues(name, description, nullSafeUUIDToString(submittedBy)));
        return opportunitiesResponse;
    }
}