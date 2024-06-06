package com.objectcomputing.checkins.services.certification;

import com.objectcomputing.checkins.exceptions.BadArgException;
import io.micronaut.core.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Singleton
class CertificationServiceImpl implements CertificationService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificationServiceImpl.class);

    private final CertificationRepository certificationRepository;
    private final EarnedCertificationRepository earnedCertificationRepository;

    CertificationServiceImpl(
            CertificationRepository certificationRepository,
            EarnedCertificationRepository earnedCertificationRepository
    ) {
        this.certificationRepository = certificationRepository;
        this.earnedCertificationRepository = earnedCertificationRepository;
    }

    @Override
    public List<Certification> findAllCertifications() {
        return certificationRepository.findAllOrderByNameAsc();
    }

    @Override
    public Certification saveCertification(Certification certification) {
        if (certification.getId() != null) {
            return updateCertification(certification);
        }
        return certificationRepository.save(certification);
    }

    @Override
    public Certification updateCertification(Certification certification) {
        return certificationRepository.update(certification);
    }

    @Override
    public List<EarnedCertification> findAllEarnedCertifications(
            @Nullable UUID memberId,
            @Nullable UUID certificationId,
            boolean includeInactive
    ) {
        if (memberId == null && certificationId == null) {
            return earnedCertificationRepository.findAllOrderByEarnedDateDesc(includeInactive);
        } else if (memberId != null && certificationId != null) {
            return earnedCertificationRepository.findByMemberIdAndCertificationIdOrderByEarnedDateDesc(memberId, certificationId, includeInactive);
        } else if (memberId != null) {
            return earnedCertificationRepository.findByMemberIdOrderByEarnedDateDesc(memberId, includeInactive);
        } else {
            return earnedCertificationRepository.findByCertificationIdOrderByEarnedDateDesc(certificationId, includeInactive);
        }
    }

    @Override
    public EarnedCertification saveEarnedCertification(EarnedCertification certification) {
        if (certification.getId() != null) {
            return updateEarnedCertification(certification);
        }
        return earnedCertificationRepository.save(certification);
    }

    @Override
    public EarnedCertification updateEarnedCertification(EarnedCertification certification) {
        return earnedCertificationRepository.update(certification);
    }

    @Override
    public void deleteEarnedCertification(UUID id) {
        earnedCertificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Certification mergeCertifications(UUID sourceId, UUID targetId) {
        Certification targetCertification = certificationRepository
                .findById(targetId)
                .orElseThrow(() -> new BadArgException("Target certification not found"));

        List<EarnedCertification> sourceCertifications = earnedCertificationRepository.findByCertificationId(sourceId);
        LOG.info("Merging {} certifications from sourceId: {}, to {}", sourceCertifications.size(), sourceId, targetId);

        // Move the earned certifications to the target certification
        earnedCertificationRepository.updateAll(sourceCertifications.stream().map(c -> c.withCertification(targetId)).toList());

        // Delete the source certification
        certificationRepository.deleteById(sourceId);
        return targetCertification;
    }
}
