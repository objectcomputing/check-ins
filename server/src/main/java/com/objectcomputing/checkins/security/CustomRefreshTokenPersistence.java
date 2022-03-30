package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.security.permissions.ExtendedUserDetails;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.PermissionServices;
import com.objectcomputing.checkins.services.refresh_token.RefreshToken;
import com.objectcomputing.checkins.services.refresh_token.RefreshTokenRepository;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleServices;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CustomRefreshTokenPersistence implements RefreshTokenPersistence {

    final Logger LOG = LoggerFactory.getLogger(CustomRefreshTokenPersistence.class);
    final MemberProfileRepository memberProfileRepo;
    final RefreshTokenRepository refreshTokenRepo;
    final PermissionServices permissionServices;
    final RoleServices roleServices;


    public CustomRefreshTokenPersistence(
            MemberProfileRepository memberProfileRepo,
            RefreshTokenRepository refreshTokenRepo,
            PermissionServices permissionServices,
            RoleServices roleServices
            )
    {
        this.memberProfileRepo = memberProfileRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.permissionServices = permissionServices;
        this.roleServices = roleServices;
    }

    @Override
    @EventListener
    public void persistToken(RefreshTokenGeneratedEvent event) {
        LOG.info("in the persist");
        LOG.info(event.getRefreshToken());
        refreshTokenRepo.save(new RefreshToken(event.getUserDetails().getUsername(), event.getRefreshToken()));
        LOG.info("saved");
    }

    @Override
    public Publisher<UserDetails> getUserDetails(String refreshToken) {
        Optional<RefreshToken> optToken = refreshTokenRepo.findByRefreshToken(refreshToken);
        LOG.info("In get userdetails");
        if (optToken.isEmpty()) {
            LOG.info("1");
            return null;
        } else {
            LOG.info("first if");
            RefreshToken token = optToken.get();
            Optional<MemberProfile> optProfile = memberProfileRepo.findByWorkEmail(token.getUserName());
            if (optProfile.isEmpty()) {
                LOG.info("2");
                return null;
            } else {
                LOG.info("Second if");
                MemberProfile profile = optProfile.get();

                LOG.info("Creating user");
                UserDetails user = createUserDetails(profile);
                LOG.info("returning publisher<user>");
                return Flowable.just(user);
            }
        }
    }

        public UserDetails createUserDetails(MemberProfile memberProfile) {
            List<Permission> permissions = permissionServices.findUserPermissions(memberProfile.getId());
            List<String> permissionsAsString = permissions.stream().map(Permission::getPermission).collect(Collectors.toList());

            Set<Role> userRoles = roleServices.findUserRoles(memberProfile.getId());
            List<String> rolesAsString = userRoles.stream().map(Role::getRole).collect(Collectors.toList());

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("permissions", permissionsAsString);
            attributes.put("email", memberProfile.getWorkEmail());
            return new UserDetails(memberProfile.getWorkEmail(), rolesAsString, attributes);
    }
}
