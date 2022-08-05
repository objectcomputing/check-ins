package com.objectcomputing.checkins.auth.operations;

import com.objectcomputing.checkins.services.commons.accessor.Accessor;
import com.objectcomputing.checkins.services.commons.accessor.AccessorSource;
import com.objectcomputing.checkins.newhire.model.Account;
import com.objectcomputing.checkins.newhire.model.AccountRole;
import com.objectcomputing.checkins.newhire.model.AccountState;
import com.objectcomputing.checkins.newhire.model.Identifiable;


import java.util.UUID;

public class AuthenticatableAccount extends Accessor implements Account, Identifiable {

    private final String identity;
    private final AccountState state;
    private final AccountRole role;

    public AuthenticatableAccount(UUID id, AccessorSource source, String identity, AccountState state, AccountRole role) {
        super(id, source);
        this.identity = identity;
        this.state = state;
        this.role = role;
    }

    @Override
    public String getIdentity() {
        return identity;
    }

    @Override
    public AccountState getState() {
        return state;
    }

    @Override
    public AccountRole getRole() {
        return role;
    }

    @Override
    public Accessor asAccessor() {
        return null;
    }
}
