package com.objectcomputing.checkins.services.referraltype;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReferralTypeTestUtil {

    public static ReferralTypeCreateDTO mkCreateReferralTypeDTO() {
        ReferralTypeCreateDTO dto = new ReferralTypeCreateDTO();
        dto.setDiscoveredOpportunity("TestReferralType");
        dto.setReferredBy("TestReferredBy");
        dto.setReferrerEmail("TestReferrerEmail");
        dto.setReferrerJobSite("TestReferrerJobSite");
        dto.setReferralTypeOther("TestReferralTypeOther");

        return dto;
    }

    // ResponseDTO here is used as an UpdateDTO
    public static ReferralTypeDTO mkUpdateReferralTypeDTO() {
        ReferralTypeDTO dto = new ReferralTypeDTO();
        dto.setDiscoveredOpportunity("TestReferralType");
        dto.setReferredBy("TestReferredBy");
        dto.setReferrerEmail("TestReferrerEmail");
        dto.setReferrerJobSite("TestReferrerJobSite");
        dto.setReferralTypeOther("TestReferralTypeOther");

        return dto;
    }

    public static ReferralType mkReferralType(String seed) {
        return new ReferralType("TestReferralType" + seed,
                "TestReferredBy" + seed,
                "TestReferrerEmail" + seed,
                "TestReferrerJobSite" + seed,
                "TestReferralTypeOther" + seed);
    }

    public static ReferralType mReferralType() { return mkReferralType(""); }

    public static void assertPreferencesEqual(ReferralType entity, ReferralTypeDTO dto) {
        assertEquals(entity.getDiscoveredOpportunity(), dto.getDiscoveredOpportunity());
        assertEquals(entity.getReferredBy(), dto.getReferredBy());
        assertEquals(entity.getReferrerEmail(), dto.getReferrerEmail());
        assertEquals(entity.getReferrerJobSite(), dto.getReferrerJobSite());
        assertEquals(entity.getReferralTypeOther(), dto.getReferralTypeOther());
    }

    public static ReferralTypeDTO toDto(ReferralType entity) {
        ReferralTypeDTO dto = new ReferralTypeDTO();
        dto.setId(entity.getId());
        dto.setDiscoveredOpportunity(entity.getDiscoveredOpportunity());
        dto.setReferredBy(entity.getReferredBy());
        dto.setReferrerEmail(entity.getReferrerEmail());
        dto.setReferrerJobSite(entity.getReferrerJobSite());
        dto.setReferralTypeOther(entity.getReferralTypeOther());

        return dto;
    }
}
