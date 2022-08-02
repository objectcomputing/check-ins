package com.objectcomputing.checkins.services.commons.account;

import com.objectcomputing.checkins.services.commons.accessor.Accessor;
import com.objectcomputing.checkins.services.commons.account.Identifiable;

public interface Account extends Identifiable {
    AccountState getState();

    AccountRole getRole();

    Accessor asAccessor();
}
