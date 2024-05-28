package com.objectcomputing.checkins.services.member_skill.skillsreport;

import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;

@Controller("/reports/skills")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skills-report")
public class SkillsReportController {
    private final SkillsReportServices skillsReportServices;

    public SkillsReportController(SkillsReportServices skillsReportServices) {
        this.skillsReportServices = skillsReportServices;
    }

    /**
     * Create a skills report from requested skills
     *
     * @param requestBody {@link SkillsReportRequestDTO} Body of the request
     * @return {@link SkillsReportResponseDTO} Returned skills report
     */
    @Post
    @RequiredPermission(Permission.CAN_VIEW_SKILLS_REPORT)
    public Mono<HttpResponse<SkillsReportResponseDTO>> reportSkills(@Body @Valid @NotNull SkillsReportRequestDTO requestBody,
                                                                    HttpRequest<?> request) {
        return Mono.fromCallable(() -> skillsReportServices.report(requestBody))
                .map(responseBody -> HttpResponse.created(responseBody)
                        .headers(headers -> headers.location(URI.create(String.format("%s", request.getPath())))));
    }
}
