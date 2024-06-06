package com.objectcomputing.checkins.services.certification;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
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

import java.net.URI;
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
    @Get
    List<Certification> findAll() {
        return certificationService.findAllCertifications();
    }

    /**
     * Create a new certification
     *
     * @param certification the certification to create
     * @param request       the {@link HttpRequest}
     * @return the created {@link Certification}
     */
    @Post
    HttpResponse<Certification> create(@Body @Valid CertificationDTO certification, HttpRequest<?> request) {
        var newCertification = certificationService.saveCertification(new Certification(certification.getName(), certification.getBadgeUrl()));

        return HttpResponse.created(newCertification)
                .headers(headers -> headers
                        .location(URI.create("%s/%s".formatted(request.getPath(), newCertification.getId())))
                );
    }

    /**
     * Update a certification
     *
     * @param id            the id of the certification to update
     * @param certification the updated certification details in the body
     * @param request       the {@link HttpRequest}
     * @return the updated {@link Certification}
     */
    @Put("/{id}")
    HttpResponse<Certification> update(@NotNull UUID id, @Body @Valid CertificationDTO certification, HttpRequest<?> request) {
        var newCertification = certificationService.updateCertification(new Certification(id, certification.getName(), certification.getBadgeUrl()));
        return HttpResponse.ok(newCertification)
                .headers(headers -> headers
                        .location(URI.create("%s/%s".formatted(request.getPath(), newCertification.getId())))
                );
    }

    /**
     * Delete a certification
     *
     * @param id the id of the certification to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    void delete(@NotNull UUID id) {
        certificationService.deleteCertification(id);
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
