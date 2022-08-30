package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.referraltype.ReferralType;

public interface ReferralTypeFixture extends RepositoryFixture {
    default ReferralType createADefaultReferralType() {
        return getReferralTypeRepository().save(new ReferralType("test", "test", "test", "test", "test"));
    }

    default ReferralType createSecondDefaultReferralType() {
        return getReferralTypeRepository().save(new ReferralType("test", "test", "test", "test", "test"));
    }
}
