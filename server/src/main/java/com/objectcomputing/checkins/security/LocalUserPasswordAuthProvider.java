package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.Environments;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleServices;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.ReactiveAuthenticationProvider;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Requires(env = {Environments.LOCAL, Environment.TEST})
public class LocalUserPasswordAuthProvider implements ReactiveAuthenticationProvider<HttpRequest<?>, String, String>  {

    private final CurrentUserServices currentUserServices;
    private final UsersStore usersStore;
    private final RoleServices roleServices;
    private final MemberRoleServices memberRoleServices;

    private final RolePermissionServices rolePermissionServices;

    public LocalUserPasswordAuthProvider(CurrentUserServices currentUserServices, UsersStore usersStore, RoleServices roleServices, MemberRoleServices memberRoleServices, RolePermissionServices rolePermissionServices) {
        this.currentUserServices = currentUserServices;
        this.usersStore = usersStore;
        this.roleServices = roleServices;
        this.memberRoleServices = memberRoleServices;
        this.rolePermissionServices = rolePermissionServices;
    }

   @Override
   @SingleResult
   public @NonNull Publisher<AuthenticationResponse> authenticate(@Nullable HttpRequest<?> requestContext, @NonNull AuthenticationRequest<String, String> authReq) {
        String email = authReq.getIdentity();
        MemberProfile memberProfile = currentUserServices.findOrSaveUser(email, email, email);

        String role;
        // if empty get default roles, otherwise create role on the fly
        if (StringUtils.isNotEmpty(role = authReq.getSecret())) {
            List<String> roles = usersStore.getUserRole(role);
            if (roles == null) {
                return Mono.just(new AuthenticationFailed(String.format("Invalid role selected %s", role)));
            }

            // remove a user from the roles they currently have (as assigned in test data)
            memberRoleServices.removeMemberFromRoles(memberProfile.getId());

            // add the roles based on role override / configuration properties
            for (String curRole : roles) {
                // if no role is found then create and save it
                Role currentRole = roleServices.findByRole(curRole).orElse(null);
                if (currentRole == null) {
                    currentRole = roleServices.save(new Role(null, curRole, "description"));
                }
                memberRoleServices.saveByIds(memberProfile.getId(), currentRole.getId());
            }
        }

        List<Permission> permissions = rolePermissionServices.findUserPermissions(memberProfile.getId());
        List<String> permissionsAsString = permissions.stream().map(Enum::name).collect(Collectors.toList());

        Set<Role> userRoles = roleServices.findUserRoles(memberProfile.getId());
        List<String> rolesAsString = userRoles.stream().map(Role::getRole).collect(Collectors.toList());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("roles", rolesAsString);
        attributes.put("permissions", permissionsAsString);
        attributes.put("email", memberProfile.getWorkEmail());

        return Mono.just(AuthenticationResponse.success(email, rolesAsString, attributes));
    }
}
