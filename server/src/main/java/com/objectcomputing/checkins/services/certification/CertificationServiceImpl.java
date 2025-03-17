package com.objectcomputing.checkins.services.certification;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionServices;
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
    private final CurrentUserServices currentUserServices;
    private final RolePermissionServices rolePermissionServices;
    private final CertificationRepository certificationRepository;
    private final EarnedCertificationRepository earnedCertificationRepository;

    CertificationServiceImpl(
            MemberProfileRepository memberProfileRepository,
            CurrentUserServices currentUserServices,
            RolePermissionServices rolePermissionServices,
            CertificationRepository certificationRepository,
            EarnedCertificationRepository earnedCertificationRepository
    ) {
        this.memberProfileRepository = memberProfileRepository;
        this.currentUserServices = currentUserServices;
        this.rolePermissionServices = rolePermissionServices;
        this.certificationRepository = certificationRepository;
        this.earnedCertificationRepository = earnedCertificationRepository;
    }

    @Override
    public List<Certification> findAllCertifications(boolean includeInactive) {
        return certificationRepository.findAllOrderByNameAsc(includeInactive);
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
    @RequiredPermission(Permission.CAN_MANAGE_CERTIFICATIONS)
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
        validateEarnedCertificate(earnedCertification, "create");
        return earnedCertificationRepository.save(earnedCertification);
    }

    @Override
    public EarnedCertification updateEarnedCertification(EarnedCertification earnedCertification) {
        validateEarnedCertificate(earnedCertification, "update");
        return earnedCertificationRepository.update(earnedCertification);
    }

    @Override
    public void deleteEarnedCertification(UUID id) {
        EarnedCertification earnedCertification = earnedCertificationRepository.findById(id).orElseThrow(() -> new BadArgException(String.format("Earned Certificate %s not found", id)));
        validatePermission(earnedCertification, "delete");
        earnedCertificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    @RequiredPermission(Permission.CAN_MANAGE_CERTIFICATIONS)
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

    private void validateEarnedCertificate(EarnedCertification earnedCertification, String action) {
        validate(memberProfileRepository.findById(earnedCertification.getMemberId()).isEmpty(), "Member %s doesn't exist", earnedCertification.getMemberId());
        validate(certificationRepository.findById(earnedCertification.getCertificationId()).isEmpty(), "Certification %s doesn't exist", earnedCertification.getCertificationId());
        validatePermission(earnedCertification, action);
    }

    private void validatePermission(EarnedCertification earnedCertification, String action) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();

        // Check if they have the admin permission
        boolean hasPermission = rolePermissionServices.findUserPermissions(currentUserId).contains(Permission.CAN_MANAGE_EARNED_CERTIFICATIONS);
        if (hasPermission) {
            return;
        }

        // Verify the userId in the request matches the current user
        if (!earnedCertification.getMemberId().equals(currentUserId)) {
            throw new BadArgException("User %s does not have permission to %s Earned Certificate for user %s".formatted(currentUserId, action, earnedCertification.getMemberId()));
        }

        // Verify the current user owns the earned certification they are trying to modify (if it exists in the db)
        Optional<UUID> dbUuid = earnedCertificationRepository.findById(earnedCertification.getId()).map(EarnedCertification::getMemberId);
        if (dbUuid.map(id -> !id.equals(currentUserId)).orElse(false)) {
            throw new BadArgException("User %s does not have permission to %s Earned Certificate for user %s".formatted(currentUserId, action, dbUuid.orElse(null)));
        }
    }

    private void validate(boolean isError, String message, Object... args) {
        if (isError) {
            throw new BadArgException(String.format(message, args));
        }
    }
}
