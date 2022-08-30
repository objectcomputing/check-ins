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

import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class CurrentUserServicesImpl implements CurrentUserServices {

    private final MemberProfileRepository memberProfileRepository;
    private final SecurityService securityService;
    private final RoleServices roleServices;
    private final MemberRoleServices memberRoleServices;

    public CurrentUserServicesImpl(MemberProfileRepository memberProfileRepository,
                                   RoleServices roleServices,
                                   SecurityService securityService,
                                   MemberRoleServices memberRoleServices) {
        this.memberProfileRepository = memberProfileRepository;
        this.roleServices = roleServices;
        this.securityService = securityService;
        this.memberRoleServices = memberRoleServices;
    }

    @Override
    public MemberProfile findOrSaveUser(@NotNull String firstName, @NotNull String lastName, @NotNull String workEmail) {
        Optional<MemberProfile> userProfile = memberProfileRepository.findByWorkEmail(workEmail);
        return userProfile.orElseGet(() -> saveNewUser(firstName, lastName, workEmail));

    }

    @Override
    public boolean hasRole(RoleType role) {
        return securityService.hasRole(role.toString());
    }

    @Override
    public boolean isAdmin() {
        return hasRole(RoleType.ADMIN);
    }

    @Override
    public boolean isHumanResources() {
        return hasRole(RoleType.HR);
    }

    public MemberProfile getCurrentUser() {

        validate(securityService != null).orElseThrow(() -> {
            throw new NotFoundException("No active members in the system");
        });

        Optional<Authentication> auth = securityService.getAuthentication();
        validate(auth.isPresent() && auth.get().getAttributes().get("email") != null).orElseThrow(() -> {
            throw new NotFoundException("No active members in the system");
        });

        String workEmail = auth.get().getAttributes().get("email").toString();
        return memberProfileRepository.findByWorkEmail(workEmail).orElse(null);
    }

    private MemberProfile saveNewUser(String firstName, String lastName, String workEmail) {
        Optional<MemberProfile> emailProfile = memberProfileRepository.findByWorkEmail(workEmail);

        validate(emailProfile.isEmpty()).orElseThrow(() -> {
            throw new AlreadyExistsException("Email %s already exists in database", workEmail);
        });

        MemberProfile createdMember = memberProfileRepository.save(new MemberProfile(firstName, null, lastName, null, "", null,
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
