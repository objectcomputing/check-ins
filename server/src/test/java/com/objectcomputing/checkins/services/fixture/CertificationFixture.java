package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.certification.Certification;
import com.objectcomputing.checkins.services.certification.EarnedCertification;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;

public interface CertificationFixture extends RepositoryFixture {

    default Certification createDefaultCertification() {
        return createCertification("Default", "Description", "Default Badge URL");
    }

    default Certification createCertification(String name, String description) {
        return createCertification(name, description, null);
    }

    default Certification createCertification(String name, String description, String badgeUrl) {
        return createCertification(name, description, badgeUrl, true);
    }

    default Certification createCertification(String name, String description, String badgeUrl, boolean active) {
        return getCertificationRepository().save(new Certification(name, description, badgeUrl, active));
    }

    default EarnedCertification createEarnedCertification(MemberProfile member, Certification certification) {
        return createEarnedCertification(member, certification, LocalDate.now());
    }

    default EarnedCertification createEarnedCertification(MemberProfile member, Certification certification, LocalDate earnedDate) {
        return getEarnedCertificationRepository().save(new EarnedCertification(
                member.getId(),
                certification.getId(),
                earnedDate,
                null,
                null
        ));
    }
}
