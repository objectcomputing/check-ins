package com.objectcomputing.checkins.auth.operations;

import com.objectcomputing.geoai.core.accessor.Accessor;
import com.objectcomputing.geoai.core.accessor.AccessorSource;
import com.objectcomputing.geoai.core.account.AccountState;
import com.objectcomputing.geoai.core.identity.Identifiable;
import com.objectcomputing.geoai.security.authentication.srp6.Srp6Credentials;

import java.util.UUID;

public class ChallengeAccount extends Accessor implements Identifiable {

    private final AccountState state;
    private final Srp6Credentials credentials;

    public ChallengeAccount(UUID id, AccessorSource source, AccountState state, Srp6Credentials credentials) {
        super(id, source);
        this.state = state;
        this.credentials = credentials;
    }

    public AccountState getState() {
        return state;
    }

    public Srp6Credentials getCredentials() {
        return credentials;
    }

    public String getIdentity() {
        return credentials.getIdentity();
    }
}
