package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileEntity;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.*;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Requires(env = "local")
public class LocalUserPasswordAuthProvider implements AuthenticationProvider {

    private CurrentUserServices currentUserServices;
    private RoleRepository roleRepository;
    private UsersStore usersStore;

    public LocalUserPasswordAuthProvider(CurrentUserServices currentUserServices, RoleRepository roleRepository, UsersStore usersStore) {
        this.currentUserServices = currentUserServices;
        this.roleRepository = roleRepository;
        this.usersStore = usersStore;
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authReq) {
        String email = authReq.getIdentity().toString();
        MemberProfileEntity memberProfileEntity = currentUserServices.findOrSaveUser(email, email);

        List<String> roles;
        String role;
        if (StringUtils.isNotEmpty(role = authReq.getSecret().toString())) {
            roles = usersStore.getUserRole(role);
            if(roles == null) {
                return Flowable.just(new AuthenticationFailed(String.format("Invalid role selected %s", role)));
            }

            List<String> currentRoles = roleRepository.findByMemberid(memberProfileEntity.getId()).stream().map(r -> r.getRole().toString()).collect(Collectors.toList());
            currentRoles.removeAll(roles);

            // Create the roles if they don't already exist, delete roles not asked for
            for (String curRole : currentRoles) {
                roleRepository.deleteByRoleAndMemberid(RoleType.valueOf(curRole), memberProfileEntity.getId());
            }

            for (String curRole : roles) {
                RoleType roleType = RoleType.valueOf(curRole);
                if (roleRepository.findByRoleAndMemberid(roleType, memberProfileEntity.getId()).isEmpty()) {
                    roleRepository.save(new Role(roleType, memberProfileEntity.getId()));
                }
            }
        } else {
            roles = roleRepository.findByMemberid(memberProfileEntity.getId()).stream().map((r) -> r.getRole().toString())
                    .collect(Collectors.toList());
        }

        return Flowable.just(new UserDetails(email, roles));
    }
}
