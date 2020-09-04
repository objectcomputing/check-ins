package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class CurrentUserServicesImpl implements CurrentUserServices {

    @Inject
    MemberProfileServices memberProfileServices;

    @Inject
    MemberProfileRepository memberProfileRepo;

    @Override
    public MemberProfile findOrSaveUser(@NotNull String name, @NotNull String workEmail) {

        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        return userProfile.orElseGet(() -> memberProfileServices.saveProfile(new MemberProfile(name, "", null,
                "", workEmail, "", null, "")));

    }

    public MemberProfile currentUserDetails(@NotNull String workEmail) {
            return memberProfileRepo.findByWorkEmail(workEmail).get();
    }
}
