package com.objectcomputing.checkins.services.survey;

import io.micronaut.core.annotation.Nullable;
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
import java.util.Set;
import java.util.UUID;


@Controller("/services/surveys")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name="survey")
public class SurveyController {

    private final SurveyService surveyResponseServices;

    public SurveyController(SurveyService surveyResponseServices) {
        this.surveyResponseServices = surveyResponseServices;
    }

    /**
     * Find survey by Name, Team Member or Date Range.
     *
     * @param name
     * @param createdBy
     * @return
     */
    @Get("/{?name,createdBy}")
    public Mono<HttpResponse<Set<Survey>>> findSurveys(@Nullable String name, @Nullable UUID createdBy) {
        return Mono.fromCallable(() -> {
            if (name!=null || createdBy!=null) {
                return surveyResponseServices.findByFields(name, createdBy);
            } else {
                return surveyResponseServices.readAll();
            }
        }).map(HttpResponse::ok);
    }

    /**
     * Create and save a new Survey.
     *
     * @param surveyResponse, {@link SurveyCreateDTO}
     * @return {@link HttpResponse<Survey>}
     */

    @Post
    public Mono<HttpResponse<Survey>> createSurvey(@Body @Valid SurveyCreateDTO surveyResponse, HttpRequest<?> request) {
        return Mono.fromCallable(() -> surveyResponseServices.save(new Survey(surveyResponse.getCreatedOn(),
                        surveyResponse.getCreatedBy(), surveyResponse.getName(), surveyResponse.getDescription())))
                .map(survey -> HttpResponse.created(survey)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), survey.getId())))));
    }

    /**
     * Update a Survey
     *
     * @param surveyResponse, {@link Survey}
     * @return {@link HttpResponse<Survey>}
     */
    @Put
    public Mono<HttpResponse<Survey>> update(@Body @Valid @NotNull Survey surveyResponse, HttpRequest<?> request) {
        return Mono.fromCallable(() -> surveyResponseServices.update(surveyResponse))
                .map(updatedSurvey -> HttpResponse.ok(updatedSurvey)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSurvey.getId())))));
    }

    /**
     * Delete A survey
     *
     * @param id, id of {@link Survey} to delete
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteSurvey(@NotNull UUID id) {
        return Mono.fromRunnable(() -> surveyResponseServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }
}
