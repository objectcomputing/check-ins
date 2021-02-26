package com.objectcomputing.checkins.services.memberprofile.currentuser;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.RoleType;
import com.objectcomputing.checkins.services.team.Team;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.utils.SecurityService;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class CurrentUserServicesImpl implements CurrentUserServices {

    private final MemberProfileRepository memberProfileRepo;
    private final SecurityService securityService;
    private final RoleServices roleServices;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public CurrentUserServicesImpl(MemberProfileRepository memberProfileRepository,
                                   RoleServices roleServices,
                                   SecurityService securityService,
                                   TeamRepository teamRepository,
                                   TeamMemberRepository teamMemberRepository) {
        this.memberProfileRepo = memberProfileRepository;
        this.roleServices = roleServices;
        this.securityService = securityService;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public MemberProfile findOrSaveUser(@Nullable String name, @NotNull String workEmail) {

        Optional<MemberProfile> userProfile = memberProfileRepo.findByWorkEmail(workEmail);
        if (userProfile.isPresent()) {
            return userProfile.get();
        }

        return saveNewUser(name, workEmail);
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
            if (auth.isPresent()) {
                String workEmail = auth.get().getAttributes().get("email").toString();
                return memberProfileRepo.findByWorkEmail(workEmail).orElse(null);
            }
        }

        throw new NotFoundException("No active members in the system");
    }

    @Override
    public boolean isCurrentUserPdlFor(UUID memberId) {
        if (!hasRole(RoleType.PDL)) {
            return false;
        }
        MemberProfile currentUser = getCurrentUser();
        MemberProfile checkUser = memberProfileRepo.findById(memberId).orElseThrow(() -> {
            throw new NotFoundException("No such member exists");
        });

        if (checkUser.getPdlId().equals(currentUser.getId())) {
            return true;
        }

        List<Team> memberTeams = teamRepository.matchMemberAndlead(memberId.toString(), currentUser.getId().toString());

        if (memberTeams.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void currentUserPdlFor(UUID memberId) {
        if (!isCurrentUserPdlFor(memberId)) {
            throw new PermissionException("User is unauthorized to do this operation");
        }
    }

    private MemberProfile saveNewUser(@Nullable String name, @NotNull String workEmail) {
        MemberProfile emailProfile = memberProfileRepo.findByWorkEmail(workEmail).orElse(null);
        if (emailProfile != null && emailProfile.getId() != null) {
            throw new AlreadyExistsException(String.format("Email %s already exists in database", workEmail));
        }

        MemberProfile createdMember = memberProfileRepo.save(new MemberProfile(name, "", null,
                "", workEmail, "", null, "", null, null));

        roleServices.save(new Role(RoleType.MEMBER, createdMember.getId()));

        return createdMember;
    }
}
