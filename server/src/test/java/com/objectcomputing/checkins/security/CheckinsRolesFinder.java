package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
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

    private MemberProfileRepository memberProfileRepository;
    private RoleRepository roleRepository;

    @Inject
    public CheckinsRolesFinder(MemberProfileRepository memberProfileRepository, RoleRepository roleRepository) {
        this.memberProfileRepository = memberProfileRepository;
        this.roleRepository = roleRepository;
    }

    @NonNull
    @Override
    public List<String> resolveRoles(@NotNull Map<String, Object> attributes) {
        List<String> roles = new ArrayList<>();
        memberProfileRepository.findByWorkEmail(attributes.get(JWTOpenIdClaims.CLAIMS_EMAIL).toString())
                .ifPresent((memberProfile) -> {
                    roles.addAll(roleRepository.findUserRoles(memberProfile.getId())
                            .stream()
                            .map(role -> role.getRole())
                            .collect(Collectors.toList()));
                });
        return roles;
    }
}
