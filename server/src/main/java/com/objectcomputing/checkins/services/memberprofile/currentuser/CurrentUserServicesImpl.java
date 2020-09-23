package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class CurrentUserServicesImpl implements CurrentUserServices {

    private MemberProfileServices memberProfileServices;
    private MemberProfileRepository memberProfileRepo;

    public CurrentUserServicesImpl(MemberProfileServices memberProfileServices, MemberProfileRepository memberProfileRepository) {
        this.memberProfileServices = memberProfileServices;
        this.memberProfileRepo = memberProfileRepository;
    }

    @Override
    public MemberProfile findOrSaveUser(@Nullable String name, @NotNull String workEmail) {

        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        return userProfile.orElseGet(() -> memberProfileServices.saveProfile(new MemberProfile(name, "", null,
                "", workEmail, "", null, "")));
    }
}
