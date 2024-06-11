package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.certification.Certification;
import com.objectcomputing.checkins.services.certification.EarnedCertification;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.time.LocalDate;

public interface CertificationFixture extends RepositoryFixture {

    default Certification createDefaultCertification() {
        return createCertification("Default", "Default Badge URL");
    }

    default Certification createCertification(String name) {
        return createCertification(name, null);
    }

    default Certification createCertification(String name, String badgeUrl) {
        return getCertificationRepository().save(new Certification(name, badgeUrl));
    }

    default EarnedCertification createEarnedCertification(MemberProfile member, Certification certification) {
        return createEarnedCertification(member, certification, "Default Earned certification", LocalDate.now());
    }

    default EarnedCertification createEarnedCertification(MemberProfile member, Certification certification, String description, LocalDate earnedDate) {
        return getEarnedCertificationRepository().save(new EarnedCertification(
                member.getId(),
                certification.getId(),
                description,
                earnedDate,
                null,
                null
        ));
    }
}
