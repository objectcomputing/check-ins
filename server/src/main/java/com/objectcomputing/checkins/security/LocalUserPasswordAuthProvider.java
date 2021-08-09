package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.*;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Requires(env = "local")
public class LocalUserPasswordAuthProvider implements AuthenticationProvider {

    private final CurrentUserServices currentUserServices;
    private final RoleRepository roleRepository;
    private final RoleServices roleServices;
    private final UsersStore usersStore;

    public LocalUserPasswordAuthProvider(CurrentUserServices currentUserServices,
                                         RoleRepository roleRepository,
                                         RoleServices roleServices,
                                         UsersStore usersStore) {
        this.currentUserServices = currentUserServices;
        this.roleRepository = roleRepository;
        this.roleServices = roleServices;
        this.usersStore = usersStore;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authReq) {
        String email = authReq.getIdentity().toString();
        MemberProfile memberProfile = currentUserServices.findOrSaveUser(email, email, email);

        List<String> roles;
        String role;
        if (StringUtils.isNotEmpty(role = authReq.getSecret().toString())) {
            roles = usersStore.getUserRole(role);
            if(roles == null) {
                return Flowable.just(new AuthenticationFailed(String.format("Invalid role selected %s", role)));
            }

            List<String> currentRoles = roleServices.findByMemberid(memberProfile.getId()).stream()
                    .map(r -> r.getRole().toString()).collect(Collectors.toList());
            currentRoles.removeAll(roles);

            // Create the roles if they don't already exist, delete roles not asked for
            for (String curRole : currentRoles) {
                roleServices.deleteByRoleAndMemberid(RoleType.valueOf(curRole), memberProfile.getId());
            }

            for (String curRole : roles) {
                RoleType roleType = RoleType.valueOf(curRole);
                if (roleServices.findByRoleAndMemberid(roleType, memberProfile.getId()).isEmpty()) {
                    roleServices.save(new RoleCreateDTO(roleType, "role description"));
                }
            }
        } else {
            roles = roleServices.findByMemberid(memberProfile.getId()).stream().map((r) -> r.getRole().toString())
                    .collect(Collectors.toList());
        }

        return Flowable.just(new UserDetails(email, roles));
    }
}
