package com.objectcomputing.checkins.services.opportunities;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class OpportunitiesServicesImpl implements OpportunitiesService {

    private final OpportunitiesRepository opportunitiesResponseRepo;
    private final MemberProfileRepository memberRepo;
    private final CurrentUserServices currentUserServices;

    public OpportunitiesServicesImpl(OpportunitiesRepository opportunitiesResponseRepo,
                                     MemberProfileRepository memberRepo,
                                     CurrentUserServices currentUserServices) {
        this.opportunitiesResponseRepo = opportunitiesResponseRepo;
        this.memberRepo = memberRepo;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Opportunities save(Opportunities opportunitiesResponse) {
        Opportunities opportunitiesResponseRet = null;
        if (opportunitiesResponse!=null){
            MemberProfile member = currentUserServices.getCurrentUser();
            if (member != null) {
                opportunitiesResponse.setSubmittedBy(member.getId());
                opportunitiesResponse.setSubmittedOn(LocalDate.now());
            }

            validate(opportunitiesResponse.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id for opportunities %s", opportunitiesResponse.getId());
            });

            opportunitiesResponseRet = opportunitiesResponseRepo.save(opportunitiesResponse);
        }
        return opportunitiesResponseRet ;
    }

    @Override
    public Opportunities update(Opportunities opportunitiesResponse) {
        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });

        Opportunities opportunitiesResponseRet = null;
        if (opportunitiesResponse != null){
            final UUID id = opportunitiesResponse.getId();
            final UUID memberId = opportunitiesResponse.getSubmittedBy();
            LocalDate surSubDate = opportunitiesResponse.getSubmittedOn();

            validate(id != null && opportunitiesResponseRepo.findById(id).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Unable to find opportunities record with id %s", opportunitiesResponse.getId());
            });
            validate(memberRepo.findById(memberId).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exist", memberId);
            });
            validate(surSubDate.isAfter(LocalDate.EPOCH) && surSubDate.isBefore(LocalDate.MAX)).orElseThrow(() -> {
                throw new BadArgException("Invalid date for opportunities submission date %s",memberId);
            });

            opportunitiesResponseRet = opportunitiesResponseRepo.update(opportunitiesResponse);
        }
        return opportunitiesResponseRet;
    }

    @Override
    public void delete(@NotNull UUID id) {
        final boolean isAdmin = currentUserServices.isAdmin();
        validate(isAdmin).orElseThrow(() -> {
            throw new PermissionException("User is unauthorized to do this operation");
        });
        opportunitiesResponseRepo.deleteById(id);
    }

    @Override
    public ArrayList<Opportunities> findByFields(String name, String description,UUID submittedBy) {
        final ArrayList<Opportunities> opportunitiesResponse = new ArrayList<>(opportunitiesResponseRepo.searchByValues(name, description, nullSafeUUIDToString(submittedBy)));
        return opportunitiesResponse;
    }
}