package com.objectcomputing.checkins.services.onboardeecreate.security.authentication;

import com.objectcomputing.checkins.services.onboardeecreate.newhire.commons.SharableNewHireAccount;

public class AuthenticatedActor {
    public String getEmailAddress() {
        SharableNewHireAccount sharableNewHireAccount = new SharableNewHireAccount();
            return sharableNewHireAccount.getEmailAddress();
    }
}
