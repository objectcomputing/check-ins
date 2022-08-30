package com.objectcomputing.checkins.security.authentication;

import com.objectcomputing.checkins.newhire.commons.SharableNewHireAccount;

public class AuthenticatedActor {
    public String getEmailAddress() {
        SharableNewHireAccount sharableNewHireAccount = new SharableNewHireAccount();
        return sharableNewHireAccount.getEmailAddress();
    }
}
