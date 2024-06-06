package com.objectcomputing.checkins.services.certification;

import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.UUID;

interface CertificationService {

    List<Certification> findAllCertifications();

    Certification saveCertification(Certification certification);

    Certification updateCertification(Certification certification);

    List<EarnedCertification> findAllEarnedCertifications(@Nullable UUID memberId, @Nullable UUID certificationId, boolean includeInactive);

    EarnedCertification saveEarnedCertification(EarnedCertification certification);

    EarnedCertification updateEarnedCertification(EarnedCertification certification);

    void deleteEarnedCertification(UUID id);

    Certification mergeCertifications(UUID sourceId, UUID targetId);
}
