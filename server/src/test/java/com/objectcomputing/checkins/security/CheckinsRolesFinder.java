package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
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

    private final MemberProfileRetrievalServices memberProfileRetrievalServices;
    private final RoleRepository roleRepository;

    @Inject
    public CheckinsRolesFinder(MemberProfileRetrievalServices memberProfileRetrievalServices, RoleRepository roleRepository) {
        this.memberProfileRetrievalServices = memberProfileRetrievalServices;
        this.roleRepository = roleRepository;
    }

    @NotNull
    @Override
    public List<String> findInClaims(@NotNull Claims openIdClaims) {
        List<String> roles = new ArrayList<>();
        memberProfileRetrievalServices.findByWorkEmail(openIdClaims.get(JWTOpenIdClaims.CLAIMS_EMAIL).toString())
                .ifPresent((memberProfile) -> {
                    roles.addAll(roleRepository.findUserRoles(memberProfile.getId())
                            .stream()
                            .map(role -> role.getRole())
                            .collect(Collectors.toList()));
                });
        return roles;
    }
}
