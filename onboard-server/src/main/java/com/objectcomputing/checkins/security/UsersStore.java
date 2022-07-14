package com.objectcomputing.checkins.security;

import com.objectcomputing.checkins.Environments;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.core.convert.format.MapFormat;
import io.micronaut.core.naming.conventions.StringConvention;

import java.util.HashMap;
import java.util.List;

@Requires(env = {Environments.LOCAL, Environment.TEST})
@ConfigurationProperties("credentials")
public class UsersStore {

    @MapFormat(keyFormat = StringConvention.UNDER_SCORE_SEPARATED, transformation = MapFormat.MapTransformation.FLAT)
    HashMap<String, List<String>> roles;

    public List<String> getUserRole(String roleCred) {
        return roleCred != null ? roles.get(roleCred) : List.of();
    }
}