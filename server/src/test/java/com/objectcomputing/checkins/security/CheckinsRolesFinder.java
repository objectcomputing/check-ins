package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRetrievalServices;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.token.DefaultRolesFinder;
import io.micronaut.security.token.RolesFinder;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @NonNull
    @Override
    public List<String> resolveRoles(@NotNull Map<String, Object> attributes) {
        List<String> roles = new ArrayList<>();
        memberProfileRetrievalServices.findByWorkEmail(attributes.get(JWTOpenIdClaims.CLAIMS_EMAIL).toString())
                .ifPresent((memberProfile) -> {
                    roles.addAll(roleRepository.findUserRoles(memberProfile.getId())
                            .stream()
                            .map(role -> role.getRole())
                            .collect(Collectors.toList()));
                });
        return roles;
    }
}
