package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServicesImpl;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Optional;

public class CurrentUserServicesImpl implements CurrentUserServices {

    @Inject
    MemberProfileRepository memberProfileRepo;

    @Inject
    MemberProfileServicesImpl memberProfileServicesImpl;

    @Override
    public MemberProfile findOrSaveUser(@NotNull String name, @NotNull String workEmail) {

        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        if (userProfile.isPresent()) {
            return userProfile.get();
        }

        return memberProfileServicesImpl.saveProfile(new MemberProfile(name,"",null,
                "", workEmail,"", null,""));
    }
}
