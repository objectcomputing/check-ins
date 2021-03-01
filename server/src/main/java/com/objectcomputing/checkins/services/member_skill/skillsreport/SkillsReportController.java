package com.objectcomputing.checkins.services.member_skill.skillsreport;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.scheduling.TaskExecutors;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.concurrent.ExecutorService;

@Controller("/reports/skills")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "skills-report")
public class SkillsReportController {
    private final SkillsReportServices skillsReportServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public SkillsReportController(SkillsReportServices skillsReportServices,
                                  EventLoopGroup eventLoopGroup,
                                  @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.skillsReportServices = skillsReportServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a skills report from requested skills
     *
     * @param requestBody {@link SkillsReportRequestDTO} Body of the request
     * @return {@link SkillsReportResponseDTO} Returned skills report
     */
    @Post()
    public Single<HttpResponse<SkillsReportResponseDTO>> reportSkills(@Body @Valid @NotNull SkillsReportRequestDTO requestBody,
                                                                      HttpRequest<SkillsReportRequestDTO> request) {
        return Single.fromCallable(() -> skillsReportServices.report(requestBody))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(responseBody -> (HttpResponse<SkillsReportResponseDTO>) HttpResponse
                        .created(responseBody)
                        .headers(headers -> headers.location(URI.create(String.format("%s", request.getPath())))))
                .subscribeOn(Schedulers.from(executorService));
    }
}
