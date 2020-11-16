package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class CurrentUserServicesImpl implements CurrentUserServices {

    private final MemberProfileServices memberProfileServices;
    private final MemberProfileRepository memberProfileRepo;
    private final RoleServices roleServices;

    public CurrentUserServicesImpl(MemberProfileServices memberProfileServices,
                                   MemberProfileRepository memberProfileRepository,
                                   RoleServices roleServices) {
        this.memberProfileServices = memberProfileServices;
        this.memberProfileRepo = memberProfileRepository;
        this.roleServices = roleServices;
    }

    @Override
    public MemberProfile findOrSaveUser(@Nullable String name, @NotNull String workEmail) {

        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        if(userProfile.isPresent()) {
            return userProfile.get();
        }

        return saveNewUser(name, workEmail);
    }

    private MemberProfile saveNewUser(@Nullable String name, @NotNull String workEmail) {
        MemberProfile user = memberProfileServices.saveProfile(new MemberProfile(name, "", null,
                "", workEmail, "", null, ""));

        roleServices.save(new Role(RoleType.MEMBER, user.getId()));

        return user;
    }
}
