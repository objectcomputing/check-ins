package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.oauth2.endpoint.token.response.JWTOpenIdClaims;
import io.micronaut.security.token.Claims;
import io.micronaut.security.token.DefaultRolesFinder;
import io.micronaut.security.token.RolesFinder;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
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

    @NotNull
    @Override
    public List<String> findInClaims(@NotNull Claims openIdClaims) {
        List<String> roles = new ArrayList<>();
        memberProfileRepository.findByWorkEmail(openIdClaims.get(JWTOpenIdClaims.SUBJECT).toString())
                .ifPresent((memberProfile) -> {
                    roles.addAll(roleRepository.findByMemberid(memberProfile.getId())
                            .stream()
                            .map(role -> role.getRole().toString())
                            .collect(Collectors.toList()));
                });
        return roles;
    }
}
