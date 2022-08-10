package com.objectcomputing.checkins.services.onboardeecreate.security.authentication.auth;

import com.objectcomputing.checkins.commons.Account;
import com.objectcomputing.checkins.commons.AccountState;
import com.objectcomputing.checkins.commons.Identifiable;

import java.util.Objects;
import java.util.UUID;

public class AuthenticatableAccount implements Account, Identifiable {

    private final UUID id;
    private final String emailAddress;
    private final AccountState state;

    public AuthenticatableAccount(UUID id, String emailAddress, AccountState state) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.state = state;
    }

    public UUID getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public AccountState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticatableAccount that = (AuthenticatableAccount) o;
        return Objects.equals(id, that.id) && Objects.equals(emailAddress, that.emailAddress) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, emailAddress, state);
    }
}
