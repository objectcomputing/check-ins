package com.objectcomputing.checkins.services.certification;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.core.annotation.Nullable;
import jakarta.transaction.Transactional;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
class CertificationServiceImpl implements CertificationService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificationServiceImpl.class);
    private static final String NAME_ALREADY_EXISTS_MESSAGE = "Certification with name %s already exists";

    private final MemberProfileRepository memberProfileRepository;
    private final CertificationRepository certificationRepository;
    private final EarnedCertificationRepository earnedCertificationRepository;

    CertificationServiceImpl(
            MemberProfileRepository memberProfileRepository,
            CertificationRepository certificationRepository,
            EarnedCertificationRepository earnedCertificationRepository
    ) {
        this.memberProfileRepository = memberProfileRepository;
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
        // Fail if a certification with the same name already exists
        validate(certificationRepository.getByName(certification.getName()).isPresent(),
                NAME_ALREADY_EXISTS_MESSAGE,
                certification.getName());
        return certificationRepository.save(certification);
    }

    @Override
    public Certification updateCertification(Certification certification) {
        // Fail if a certification with the same name already exists (but it's not this one)
        validate(certificationRepository.getByName(certification.getName())
                        .map(c -> !c.getId().equals(certification.getId())).orElse(false),
                NAME_ALREADY_EXISTS_MESSAGE,
                certification.getName());
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
    public EarnedCertification saveEarnedCertification(EarnedCertification earnedCertification) {
        if (earnedCertification.getId() != null) {
            return updateEarnedCertification(earnedCertification);
        }
        validateEarnedCertificate(earnedCertification);
        return earnedCertificationRepository.save(earnedCertification);
    }

    @Override
    public EarnedCertification updateEarnedCertification(EarnedCertification earnedCertification) {
        validateEarnedCertificate(earnedCertification);
        return earnedCertificationRepository.update(earnedCertification);
    }

    private void validateEarnedCertificate(EarnedCertification earnedCertification) {
        validate(memberProfileRepository.findById(earnedCertification.getMemberId()).isEmpty(), "Member %s doesn't exist", earnedCertification.getMemberId());
        validate(certificationRepository.findById(earnedCertification.getCertificationId()).isEmpty(), "Certification %s doesn't exist", earnedCertification.getCertificationId());
    }

    @Override
    public void deleteEarnedCertification(UUID id) {
        earnedCertificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Certification mergeCertifications(UUID sourceId, UUID targetId) {
        Optional<Certification> target = certificationRepository.findById(targetId);
        Optional<Certification> source = certificationRepository.findById(sourceId);

        validate(target.isEmpty(), "Target certification %s not found", targetId);
        validate(source.isEmpty(), "Source certification %s not found", sourceId);

        Certification targetCertification = target.get();

        List<EarnedCertification> sourceCertifications = earnedCertificationRepository.findByCertificationId(sourceId);
        LOG.info("Merging {} certifications from sourceId: {}, to {}", sourceCertifications.size(), sourceId, targetId);

        // Move the earned certifications to the target certification
        earnedCertificationRepository.updateAll(sourceCertifications.stream().map(c -> c.withCertification(targetId)).toList());

        // Delete the source certification
        certificationRepository.deleteById(sourceId);
        return targetCertification;
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}
