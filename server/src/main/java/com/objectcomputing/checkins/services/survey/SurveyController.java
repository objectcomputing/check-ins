package com.objectcomputing.checkins.services.survey;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.inject.Named;
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
    public Single<HttpResponse<Set<Survey>>> findSurveys(@Nullable String name,
                                                                       @Nullable UUID createdBy) {
        return Single.fromCallable(() -> {
            if (name!=null || createdBy!=null) {
                return surveyResponseServices.findByFields(name, createdBy);
            } else {
                return surveyResponseServices.readAll();
            }
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(survey -> (HttpResponse<Set<Survey>>) HttpResponse.ok(survey))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Create and save a new Survey.
     *
     * @param surveyResponse, {@link SurveyCreateDTO}
     * @return {@link HttpResponse<Survey>}
     */

    @Post()
    public Single<HttpResponse<Survey>> createSurvey(@Body @Valid SurveyCreateDTO surveyResponse,
                                                                   HttpRequest<SurveyCreateDTO> request) {
        return Single.fromCallable(() -> surveyResponseServices.save(new Survey(surveyResponse.getCreatedOn(), surveyResponse.getCreatedBy(), surveyResponse.getName(), surveyResponse.getDescription())))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(survey -> {return (HttpResponse<Survey>) HttpResponse
                        .created(survey)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), survey.getId()))));
                }).subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update a Survey
     *
     * @param surveyResponse, {@link Survey}
     * @return {@link HttpResponse<Survey>}
     */
    @Put()
    public Single<HttpResponse<Survey>> update(@Body @Valid @NotNull Survey surveyResponse,
                                                      HttpRequest<Survey> request) {
        return Single.fromCallable(() -> surveyResponseServices.update(surveyResponse))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updatedSurvey -> (HttpResponse<Survey>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSurvey.getId()))))
                        .body(updatedSurvey))
                .subscribeOn(Schedulers.from(ioExecutorService));

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
