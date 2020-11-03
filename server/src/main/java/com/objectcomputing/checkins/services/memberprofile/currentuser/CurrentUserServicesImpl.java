package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
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

    private MemberProfileServices memberProfileServices;
    private MemberProfileRepository memberProfileRepo;
    private RoleServices roleServices;

    public CurrentUserServicesImpl(MemberProfileServices memberProfileServices,
                                   MemberProfileRepository memberProfileRepository,
                                   RoleServices roleServices) {
        this.memberProfileServices = memberProfileServices;
        this.memberProfileRepo = memberProfileRepository;
        this.roleServices = roleServices;
    }

    @Override
    public MemberProfileEntity findOrSaveUser(@Nullable String name, @NotNull String workEmail) {

        Optional<MemberProfileEntity> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        if(userProfile.isPresent()) {
            return userProfile.get();
        }

        return saveNewUser(name, workEmail);
    }

    private MemberProfileEntity saveNewUser(@Nullable String name, @NotNull String workEmail) {
        MemberProfileEntity user = memberProfileServices.saveProfile(new MemberProfileEntity(name, "", null,
                "", workEmail, "", null, ""));

        roleServices.save(new Role(RoleType.MEMBER, user.getId()));

        return user;
    }
}
