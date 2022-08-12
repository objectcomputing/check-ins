package com.objectcomputing.checkins.auth.operations;

import com.objectcomputing.checkins.security.authentication.srp6.Srp6Credentials;
import com.objectcomputing.checkins.commons.AccountState;
import com.objectcomputing.checkins.commons.Identifiable;

import java.util.UUID;

public class ChallengeAccount implements Identifiable {

    private final UUID id;

    private final AccountState state;
    private final Srp6Credentials credentials;

    public ChallengeAccount(UUID id, AccountState state, Srp6Credentials credentials) {
        this.id = id;
        this.state = state;
        this.credentials = credentials;
    }

    public UUID getId() {
        return id;
    }

    public AccountState getState() {
        return state;
    }

    public Srp6Credentials getCredentials() {
        return credentials;
    }

    public String getEmailAddress() {
        return credentials.getEmailAddress();
    }
}
