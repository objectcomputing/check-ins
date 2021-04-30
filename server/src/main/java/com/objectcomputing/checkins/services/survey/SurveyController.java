package com.objectcomputing.checkins.services.survey;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.survey.Survey;
import com.objectcomputing.checkins.services.survey.SurveyCreateDTO;
import com.objectcomputing.checkins.services.survey.SurveyService;
import io.micronaut.core.convert.format.Format;
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
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/survey")
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
     * Find survey by Team Member or Date Range.
     *
     * @param createdBy
     * @param dateFrom
     * @param dateTo
     * @return
     */
    @Get("/{?createdBy,dateFrom,dateTo}")
    public Single<HttpResponse<Set<Survey>>> findSurveys(@Nullable @Format("yyyy-MM-dd") LocalDate dateFrom,
                                                                       @Nullable @Format("yyyy-MM-dd") LocalDate dateTo,
                                                                       @Nullable UUID createdBy) {
        return Single.fromCallable(() -> surveyResponseServices.findByFields(createdBy, dateFrom, dateTo))
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
     *
     * @param id
     * @return
     */
    @Get("/{id}")
    public Single<HttpResponse<Survey>> readRole(@NotNull UUID id) {
        return Single.fromCallable(() -> {
            Survey result = surveyResponseServices.read(id);
            if (result == null) {
                throw new NotFoundException("No role item for UUID");
            }
            return result;
        })
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(survey -> {
                    return (HttpResponse<Survey>)HttpResponse.ok(survey);
                }).subscribeOn(Schedulers.from(ioExecutorService));

    }
}
