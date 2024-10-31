package com.objectcomputing.checkins.services.survey;

import io.micronaut.core.annotation.Nullable;
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
import java.util.Set;
import java.util.UUID;

@Controller("/services/surveys")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "survey")
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
    public Set<Survey> findSurveys(@Nullable String name, @Nullable UUID createdBy) {
        if (name != null || createdBy != null) {
            return surveyResponseServices.findByFields(name, createdBy);
        } else {
            return surveyResponseServices.readAll();
        }
    }

    /**
     * Create and save a new Survey.
     *
     * @param surveyResponse, {@link SurveyCreateDTO}
     * @return {@link HttpResponse<Survey>}
     */
    @Post
    public HttpResponse<Survey> createSurvey(@Body @Valid SurveyCreateDTO surveyResponse, HttpRequest<?> request) {
        Survey survey = surveyResponseServices.save(new Survey(surveyResponse.getCreatedOn(),
                surveyResponse.getCreatedBy(), surveyResponse.getName(), surveyResponse.getDescription()));
        return HttpResponse.created(survey)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), survey.getId()))));
    }

    /**
     * Update a Survey
     *
     * @param surveyResponse, {@link Survey}
     * @return {@link HttpResponse<Survey>}
     */
    @Put
    public HttpResponse<Survey> update(@Body @Valid @NotNull Survey surveyResponse, HttpRequest<?> request) {
        Survey updatedSurvey = surveyResponseServices.update(surveyResponse);
        return HttpResponse.ok(updatedSurvey)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedSurvey.getId()))));
    }

    /**
     * Delete A survey
     *
     * @param id, id of {@link Survey} to delete
     */
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteSurvey(@NotNull UUID id) {
        surveyResponseServices.delete(id);
    }
}
