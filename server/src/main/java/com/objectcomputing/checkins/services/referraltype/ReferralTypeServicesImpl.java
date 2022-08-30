package com.objectcomputing.checkins.services.referraltype;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

@Singleton
public class ReferralTypeServicesImpl implements ReferralTypeServices {

    private final ReferralTypeRepository referralTypeRepository;
    public ReferralTypeServicesImpl(ReferralTypeRepository referralTypeRepository) {
        this.referralTypeRepository = referralTypeRepository;
    }

    @Override
    public ReferralType getById(@NotNull UUID id) {
        Optional<ReferralType> referralType = referralTypeRepository.findById(id);
        if (referralType.isEmpty()) {
            throw new NotFoundException("No new referral type for id " + id);
        }
        return referralType.get();
    }

    @Override
    public Set<ReferralType> findByValues(UUID id, String discoveredOpportunity, String referredBy, String referrerEmail, String referrerJobSite, String referralTypeOther) {
        HashSet<ReferralType> referral_types = new HashSet<>(referralTypeRepository.search((nullSafeUUIDToString(id)), discoveredOpportunity,
                referredBy, referrerEmail, referrerJobSite, referralTypeOther));

        return referral_types;
    }

    @Override
    public ReferralType saveReferralType(ReferralType referral_type) {
        if (referral_type.getId() == null) {
            return referralTypeRepository.save(referral_type);
        }
        return referralTypeRepository.update(referral_type);
    }

    @Override
    public Boolean deleteReferralType(UUID id) {
        referralTypeRepository.deleteById(id);
        return true;
    }

    @Override
    public ReferralType findByReferrer(String referredBy) {
        List<ReferralType> searchResult = referralTypeRepository.search(null, null, referredBy, null, null, null);
        if (searchResult.size() != 1) {
            throw new BadArgException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }

    @Override
    public List<ReferralType> findAll() {
        return referralTypeRepository.findAll();
    }


}
