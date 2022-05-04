package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.token.Claims;
import io.micronaut.security.token.DefaultRolesFinder;
import io.micronaut.security.token.RolesFinder;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Replaces(DefaultRolesFinder.class)
public class CheckinsRolesFinder implements RolesFinder {

    private MemberProfileServices memberProfileServices;
    private RoleRepository roleRepository;

    @Inject
    public CheckinsRolesFinder(MemberProfileServices memberProfileServices, RoleRepository roleRepository) {
        this.memberProfileServices = memberProfileServices;
        this.roleRepository = roleRepository;
    }

    @NotNull
    @Override
    public List<String> findInClaims(@NotNull Claims openIdClaims) {
        List<String> roles = new ArrayList<>();
        memberProfileServices.findByWorkEmail(openIdClaims.get(JWTOpenIdClaims.CLAIMS_EMAIL).toString())
                .ifPresent((memberProfile) -> {
                    roles.addAll(roleRepository.findUserRoles(memberProfile.getId())
                            .stream()
                            .map(role -> role.getRole())
                            .collect(Collectors.toList()));
                });
        return roles;
    }
}
