package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServicesImpl;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class CurrentUserServicesImpl implements CurrentUserServices {

    @Inject
    MemberProfileServicesImpl memberProfileServicesImpl;

    @Inject
    MemberProfileRepository memberProfileRepo;

    @Override
    public MemberProfile findOrSaveUser(@NotNull String name, @NotNull String workEmail) {

        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        return userProfile.orElseGet(() -> memberProfileServicesImpl.saveProfile(new MemberProfile(name, "", null,
                "", workEmail, "", null, "")));

    }
}
