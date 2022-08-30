package com.objectcomputing.checkins.security.authorization;

import java.util.Objects;
import java.util.UUID;

public class AuthorizationToken {
    private final UUID id;
    private final String token;

    public AuthorizationToken(UUID id, String token) {
        this.id = id;
        this.token = token;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationToken that = (AuthorizationToken) o;
        return Objects.equals(id, that.id) && Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token);
    }
}
