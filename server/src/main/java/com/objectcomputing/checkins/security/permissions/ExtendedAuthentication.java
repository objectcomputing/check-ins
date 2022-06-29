package com.objectcomputing.checkins.security.permissions;

import io.micronaut.security.authentication.ServerAuthentication;

import java.util.Collection;
import java.util.Map;

public class ExtendedAuthentication extends ServerAuthentication {

    private Collection<String> permissions;

    public ExtendedAuthentication(String username, Collection<String> roles, Map<String, Object> attributes) {
        super(username, roles, attributes);
        this.permissions = (Collection<String>) attributes.get("permissions");
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = permissions;
    }
}
