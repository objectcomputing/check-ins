package com.objectcomputing.checkins.newhire.model;

public interface Account extends Identifiable {
    AccountState getState();

    AccountRole getRole();
}
