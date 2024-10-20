package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.PermissionServices;
import com.objectcomputing.checkins.services.refresh_token.RefreshToken;
import com.objectcomputing.checkins.services.refresh_token.RefreshTokenRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
public class CustomRefreshTokenPersistence implements RefreshTokenPersistence {

    private static final Logger LOG = LoggerFactory.getLogger(CustomRefreshTokenPersistence.class);

    final MemberProfileRepository memberProfileRepo;
    final RefreshTokenRepository refreshTokenRepo;
    final PermissionServices permissionServices;
    final RoleServices roleServices;

    final RolePermissionServices rolePermissionServices;

    public CustomRefreshTokenPersistence(MemberProfileRepository memberProfileRepo, RefreshTokenRepository refreshTokenRepo, PermissionServices permissionServices, RoleServices roleServices, RolePermissionServices rolePermissionServices) {
        this.memberProfileRepo = memberProfileRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.permissionServices = permissionServices;
        this.roleServices = roleServices;
        this.rolePermissionServices = rolePermissionServices;
    }

    @Override
    @EventListener
    public void persistToken(RefreshTokenGeneratedEvent event) {
        LOG.info("in the persist");
        refreshTokenRepo.save(new RefreshToken(event.getAuthentication().getName(), event.getRefreshToken()));
    }

    @Override
    public Publisher<Authentication> getAuthentication(String refreshToken) {
        Optional<RefreshToken> optToken = refreshTokenRepo.findByRefreshToken(refreshToken);
        if (optToken.isEmpty()) {
            return null;
        } else {
            RefreshToken token = optToken.get();
            Optional<MemberProfile> optProfile = memberProfileRepo.findByWorkEmail(token.getUserName());
            if (optProfile.isEmpty()) {
                return null;
            } else {
                MemberProfile profile = optProfile.get();

                Authentication auth = createAuthentication(profile);
                return Mono.just(auth);
            }
        }
    }

    public Authentication createAuthentication(MemberProfile memberProfile) {
        List<Permission> permissions = rolePermissionServices.findUserPermissions(memberProfile.getId());
        List<String> permissionsAsString = permissions.stream().map(Permission::name).toList();

        Set<Role> userRoles = roleServices.findUserRoles(memberProfile.getId());
        List<String> rolesAsString = userRoles.stream().map(Role::getRole).toList();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("permissions", permissionsAsString);
        attributes.put("email", memberProfile.getWorkEmail());

        return Authentication.build(memberProfile.getWorkEmail(), rolesAsString, attributes);
    }
}
