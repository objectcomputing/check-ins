package com.objectcomputing.checkins.services.certification;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/services/earned-certification")
@Tag(name = "certification")
class EarnedCertificationController {

    private final CertificationService certificationService;

    EarnedCertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    /**
     * List all earned certifications.
     * If memberId is provided, restrict to earned certifications for that member.
     * If certificationId is provided, restrict to earned certifications for that certification.
     * If includeInactive is true, include inactive certifications in the results.
     *
     * @param memberId        the id of the member to list earned certifications for
     * @param certificationId the id of the certification to list earned certifications for
     * @param includeInactive whether to include inactive certifications in the results (defalts to false)
     * @return a List of {@link EarnedCertification}
     */
    @Get("{?memberId,certificationId,includeInactive}")
    List<EarnedCertification> findAll(@Nullable UUID memberId, @Nullable UUID certificationId, @Nullable Boolean includeInactive) {
        return certificationService.findAllEarnedCertifications(memberId, certificationId, Boolean.TRUE.equals(includeInactive));
    }

    /**
     * Create a new earned certification
     *
     * @param certification the {@link EarnedCertificationDTO} specifying the {@link EarnedCertification} to create
     * @param request       the {@link HttpRequest}
     * @return the newly created {@link EarnedCertification}
     */
    @Post
    @Status(HttpStatus.CREATED)
    EarnedCertification create(@Body @Valid EarnedCertificationDTO certification, HttpRequest<?> request) {
        return certificationService.saveEarnedCertification(
                new EarnedCertification(
                        certification.getMemberId(),
                        certification.getCertificationId(),
                        certification.getEarnedDate(),
                        certification.getExpirationDate(),
                        certification.getValidationUrl()
                )
        );
    }

    /**
     * Update an earned certification
     *
     * @param id            the id of the earned certification to update
     * @param certification the {@link EarnedCertificationDTO} specifying the updated details
     * @param request       the {@link HttpRequest}
     * @return the updated {@link EarnedCertification}
     */
    @Put("/{id}")
    EarnedCertification update(@NotNull UUID id, @Body @Valid EarnedCertificationDTO certification, HttpRequest<?> request) {
        return certificationService.updateEarnedCertification(
                new EarnedCertification(
                        id,
                        certification.getMemberId(),
                        certification.getCertificationId(),
                        certification.getEarnedDate(),
                        certification.getExpirationDate(),
                        certification.getValidationUrl()
                )
        );
    }

    /**
     * Delete an earned certification
     *
     * @param id the id of the earned certification to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    void deleteEarnedCertification(@NotNull UUID id) {
        certificationService.deleteEarnedCertification(id);
    }
}
