package com.objectcomputing.checkins.services.survey;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


@Controller("/services/surveys")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="survey")

public class SurveyController {

    private final SurveyService surveyResponseServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public SurveyController(SurveyService surveyResponseServices,
                                   EventLoopGroup eventLoopGroup,
                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.surveyResponseServices = surveyResponseServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Find survey by Name, Team Member or Date Range.
     *
     * @param name
     * @param createdBy
     * @return
     */
    @Get("/{?name,createdBy}")
    public Mono<HttpResponse<Set<Survey>>> findSurveys(@Nullable String name,
                                                       @Nullable UUID createdBy) {
        return Mono.fromCallable(() -> {
            if (name!=null || createdBy!=null) {
                return surveyResponseServices.findByFields(name, createdBy);
            } else {
                return surveyResponseServices.readAll();
            }
        })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(survey -> (HttpResponse<Set<Survey>>) HttpResponse.ok(survey))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Create and save a new Survey.
     *
     * @param surveyResponse, {@link SurveyCreateDTO}
     * @return {@link HttpResponse<Survey>}
     */

    @Post()
    public Mono<HttpResponse<Survey>> createSurvey(@Body @Valid SurveyCreateDTO surveyResponse,
                                                                   HttpRequest<SurveyCreateDTO> request) {
        return Mono.fromCallable(() -> surveyResponseServices.save(new Survey(surveyResponse.getCreatedOn(), surveyResponse.getCreatedBy(), surveyResponse.getName(), surveyResponse.getDescription())))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(survey -> {return (HttpResponse<Survey>) HttpResponse
                        .created(survey)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), survey.getId()))));
                }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update a Survey
     *
     * @param surveyResponse, {@link Survey}
     * @return {@link HttpResponse<Survey>}
     */
    @Put()
    public Mono<HttpResponse<Survey>> update(@Body @Valid @NotNull Survey surveyResponse,
                                                      HttpRequest<Survey> request) {
        return Mono.fromCallable(() -> surveyResponseServices.update(surveyResponse))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedSurvey -> (HttpResponse<Survey>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSurvey.getId()))))
                        .body(updatedSurvey))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Delete A survey
     *
     * @param id, id of {@link Survey} to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteSurvey(@NotNull UUID id) {
        surveyResponseServices.delete(id);
        return HttpResponse
                .ok();
    }
}
