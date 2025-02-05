package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;

@Controller("/reports/skills")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
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
    public HttpResponse<SkillsReportResponseDTO> reportSkills(@Body @Valid @NotNull SkillsReportRequestDTO requestBody,
                                                              HttpRequest<?> request) {
        SkillsReportResponseDTO responseBody = skillsReportServices.report(requestBody);
        return HttpResponse.created(responseBody)
                .headers(headers -> headers.location(URI.create(String.format("%s", request.getPath()))));
    }
}
