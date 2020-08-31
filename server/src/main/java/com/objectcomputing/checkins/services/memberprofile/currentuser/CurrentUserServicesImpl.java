package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.checkins.CheckInBadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class CurrentUserServicesImpl implements CurrentUserServices {

    @Inject
    MemberProfileServices memberProfileServices;

    @Inject
    MemberProfileRepository memberProfileRepo;

    @Inject
    SecurityService securityService;

    @Override
    public MemberProfile findOrSaveUser(@NotNull String name, @NotNull String workEmail) {

        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        return userProfile.orElseGet(() -> memberProfileServices.saveProfile(new MemberProfile(name, "", null,
                "", workEmail, "", null, "")));

    }

    public MemberProfile currentUserDetails() {

        if(securityService != null && securityService.getAuthentication().isPresent()) {
            Authentication authentication = securityService.getAuthentication().get();
            String workEmail = authentication.getAttributes().get("email").toString();
            return memberProfileRepo.findByWorkEmail(workEmail).get();
        }

        throw new CheckInBadArgException("Unauthorized user");
    }
}
