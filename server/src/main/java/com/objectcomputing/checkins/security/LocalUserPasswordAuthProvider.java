package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Requires(env = "local")
@Replaces(UserPasswordAuthProvider.class)
public class LocalUserPasswordAuthProvider implements AuthenticationProvider {

    @Inject
    private CurrentUserServices currentUserServices;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    UsersStore usersStore;

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authReq) {
        String email = authReq.getIdentity().toString();
        MemberProfile memberProfile = currentUserServices.findOrSaveUser(email, email);

        List<String> roles;
        String role;
        if (StringUtils.isNotEmpty(role = authReq.getSecret().toString())) {
            roles = usersStore.getUserRole(role);
            if(roles == null) {
                return Flowable.just(new AuthenticationFailed(String.format("Invalid role selected %s", role)));
            }

            List<String> currentRoles = roleRepository.findByMemberid(memberProfile.getId()).stream().map(r -> r.getRole().toString()).collect(Collectors.toList());
            currentRoles.removeAll(roles);

            // Create the roles if they don't already exist, delete roles not asked for
            for (String curRole : currentRoles) {
                roleRepository.deleteByRoleAndMemberid(RoleType.valueOf(curRole), memberProfile.getId());
            }

            for (String curRole : roles) {
                RoleType roleType = RoleType.valueOf(curRole);
                if (roleRepository.findByRoleAndMemberid(roleType, memberProfile.getId()).isEmpty()) {
                    roleRepository.save(new Role(roleType, memberProfile.getId()));
                }
            }
        } else {
            roles = roleRepository.findByMemberid(memberProfile.getId()).stream().map((r) -> r.getRole().toString())
                    .collect(Collectors.toList());
        }

        return Flowable.just(new UserDetails(email, roles));
    }
}
