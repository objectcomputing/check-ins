package com.objectcomputing.checkins.security.permissions;

import java.util.List;
import java.util.Objects;

public class KeycloakUser {
    private String email;
    private String username;
    private List<String> roles;

    @Override
    public String toString() {
        return "KeycloakUser{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", roles=" + roles +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        KeycloakUser that = (KeycloakUser) o;
        return Objects.equals(email, that.email) && Objects.equals(username, that.username)
                && Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, username, roles);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public KeycloakUser(String email, String username, List<String> roles) {
        this.email = email;
        this.username = username;
        this.roles = roles;
    }
}
