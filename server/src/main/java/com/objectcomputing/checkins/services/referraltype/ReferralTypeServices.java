package com.objectcomputing.checkins.services.referraltype;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ReferralTypeServices {
    ReferralType getById(UUID id);

    Set<ReferralType> findByValues (UUID id, String discoveredOpportunity, String referredBy,
                                                     String referrerEmail, String referrerJobSite, String referralTypeOther);

    ReferralType saveReferralType(ReferralType referralType);

    Boolean deleteReferralType(UUID id);

    ReferralType findByReferrer(@NotNull String referredBy);

    List<ReferralType> findAll();
}
