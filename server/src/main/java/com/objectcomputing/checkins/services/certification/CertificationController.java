package com.objectcomputing.checkins.services.certification;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/services/certification")
@Tag(name = "certification")
class CertificationController {

    private final CertificationService certificationService;

    CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    /**
     * List all certifications
     *
     * @return a List of {@link Certification}
     */
    @Get("{?includeInactive}")
    List<Certification> findAll(@Nullable Boolean includeInactive) {
        return certificationService.findAllCertifications(Boolean.TRUE.equals(includeInactive));
    }

    /**
     * Create a new certification
     *
     * @param certification the certification to create
     * @return the created {@link Certification}
     */
    @Post
    @Status(HttpStatus.CREATED)
    Certification create(@Body @Valid CertificationDTO certification) {
        return certificationService.saveCertification(new Certification(
                certification.getName(),
                certification.getDescription(),
                certification.getBadgeUrl(),
                !Boolean.FALSE.equals(certification.getActive())
        ));
    }

    /**
     * Update a certification
     *
     * @param id            the id of the certification to update
     * @param certification the updated certification details in the body
     * @return the updated {@link Certification}
     */
    @Put("/{id}")
    Certification update(@NotNull UUID id, @Body @Valid CertificationDTO certification) {
        return certificationService.updateCertification(new Certification(
                id,
                certification.getName(),
                certification.getDescription(),
                certification.getBadgeUrl(),
                !Boolean.FALSE.equals(certification.getActive())
        ));
    }

    /**
     * Given a source and target certification, move all earned certifications from the source to the target and delete the source certification.
     *
     * @param certificationMergeDTO the source and target certifications to merge
     * @return the merged {@link Certification}
     */
    @Post("/merge")
    Certification mergeCertifications(@Valid @Body CertificationMergeDTO certificationMergeDTO) {
        return certificationService.mergeCertifications(certificationMergeDTO.getSourceId(), certificationMergeDTO.getTargetId());
    }
}
