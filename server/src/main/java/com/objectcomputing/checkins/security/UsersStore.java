package com.objectcomputing.checkins.security;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.convert.format.MapFormat;
import io.micronaut.core.naming.conventions.StringConvention;

import java.util.*;

@Requires(env = {"local", "test"})
@ConfigurationProperties("credentials")
public class UsersStore {

    @MapFormat(keyFormat = StringConvention.UNDER_SCORE_SEPARATED, transformation = MapFormat.MapTransformation.FLAT)
    HashMap<String, List<String>> roles;

    public String getUserPassword(String username) {
        return username;
    }

    public List<String> getUserRole(String username) {
        return username != null ? roles.get(username) : List.of();
    }
}