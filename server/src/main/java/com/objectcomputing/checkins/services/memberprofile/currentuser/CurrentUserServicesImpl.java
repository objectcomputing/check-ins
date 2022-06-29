package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleServices;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class CurrentUserServicesImpl implements CurrentUserServices {

    private final MemberProfileRepository memberProfileRepo;
    private final SecurityService securityService;
    private final RoleServices roleServices;
    private final MemberRoleServices memberRoleServices;

    public CurrentUserServicesImpl(MemberProfileRepository memberProfileRepository,
                                   RoleServices roleServices,
                                   SecurityService securityService, MemberRoleServices memberRoleServices) {
        this.memberProfileRepo = memberProfileRepository;
        this.roleServices = roleServices;
        this.securityService = securityService;
        this.memberRoleServices = memberRoleServices;
    }

    @Override
    public MemberProfile findOrSaveUser(@NotNull String firstName, @NotNull String lastName, @NotNull String workEmail) {
        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        if (userProfile.isPresent()) {
            return userProfile.get();
        }

        return saveNewUser(firstName, lastName, workEmail);
    }

    @Override
    public boolean hasRole(RoleType role) {
        return securityService.hasRole(role.toString());
    }

    @Override
    public boolean isAdmin() {
        return hasRole(RoleType.ADMIN);
    }

    public MemberProfile getCurrentUser() {
        if (securityService != null) {
            Optional<Authentication> auth = securityService.getAuthentication();
            if (auth.isPresent() && auth.get().getAttributes().get("email") != null) {
                String workEmail = auth.get().getAttributes().get("email").toString();
                return memberProfileRepo.findByWorkEmail(workEmail).orElse(null);
            }
        }

        throw new NotFoundException("No active members in the system");
    }

    private MemberProfile saveNewUser(String firstName, String lastName, String workEmail) {
        MemberProfile emailProfile = memberProfileRepo.findByWorkEmail(workEmail).orElse(null);
        if (emailProfile != null) {
            throw new AlreadyExistsException(String.format("Email %s already exists in database", workEmail));
        }

        MemberProfile createdMember = memberProfileRepo.save(new MemberProfile(firstName, null, lastName, null, "", null,
                "", workEmail, "", null, "", null, null, null, null, null));

        Optional<Role> role = roleServices.findByRole("MEMBER");
        if(role.isPresent()){
            memberRoleServices.saveByIds(createdMember.getId(), role.get().getId());
        } else{
            Role memberRole = roleServices.save(new Role(RoleType.MEMBER.name(), "role description"));
            memberRoleServices.saveByIds(createdMember.getId(), memberRole.getId());
        }

        return createdMember;
    }
}
