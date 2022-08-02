package com.objectcomputing.checkins.services.commons.account;

public enum AccountRole {
    Administrator(true, true),
    Account(true, true),
    Unknown(false, false);


    private final boolean account;
    private final boolean administrator;

    AccountRole(boolean account, boolean administrator) {
        this.account = account;
        this.administrator = administrator;
    }

    public boolean isAccount() {
        return account;
    }

    public boolean isAdministrator() {
        return administrator;
    }
}
