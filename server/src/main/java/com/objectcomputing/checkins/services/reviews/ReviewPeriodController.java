package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.Status;
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

@Controller("/services/review-periods")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "reviews")
public class ReviewPeriodController {

    private final ReviewPeriodServices reviewPeriodServices;

    public ReviewPeriodController(ReviewPeriodServices reviewPeriodServices) {
        this.reviewPeriodServices = reviewPeriodServices;
    }

    /**
     * Create and save a new {@link ReviewPeriod}.
     *
     * @param period  a {@link ReviewPeriodCreateDTO} representing the desired review period
     * @return a streamable response containing the stored {@link ReviewPeriod}
     */
    @Post
    @RequiredPermission(Permission.CAN_CREATE_REVIEW_PERIOD)
    public HttpResponse<ReviewPeriod> createReviewPeriod(@Body @Valid ReviewPeriodCreateDTO period, HttpRequest<?> request) {
        ReviewPeriod reviewPeriod = reviewPeriodServices.save(period.convertToEntity());
        return HttpResponse.created(reviewPeriod)
                        .headers(headers -> headers
                                .location(URI.create(String.format("%s/%s", request.getPath(), reviewPeriod.getId())))
                        );
    }

    /**
     * Retrieve a {@link ReviewPeriod} given its id.
     *
     * @param id {@link UUID} of the review entry
     * @return a streamable response containing the found {@link ReviewPeriod} with the given ID
     */
    @Get("/{id}")
    public ReviewPeriod getById(@NotNull UUID id) {
        ReviewPeriod result = reviewPeriodServices.findById(id);
        if (result == null) {
            throw new NotFoundException("No review period for UUID");
        }
        return result;
    }

    /**
     * Find {@link ReviewPeriod}s by name and/or open status, if both are blank get all review periods.
     *
     * @param name, name of the review period
     * @param reviewStatus, the current {@link ReviewStatus} of the review (
     * @return a streamable response containing a {@link Set} of {@link ReviewPeriod}s that match the given criteria
     */
    @Get("/{?name,reviewStatus}")
    public Set<ReviewPeriod> findByValue(@Nullable String name, @Nullable ReviewStatus reviewStatus) {
        return reviewPeriodServices.findByValue(name, reviewStatus);
    }

    /**
     * Update an existing {@link ReviewPeriod}.
     *
     * @param reviewPeriod  the updated {@link ReviewPeriod}
     * @return a streamable response containing the stored {@link ReviewPeriod}
     */
    @Put
    @RequiredPermission(Permission.CAN_UPDATE_REVIEW_PERIOD)
    public HttpResponse<ReviewPeriod> update(@Body @Valid ReviewPeriod reviewPeriod, HttpRequest<?> request) {
        ReviewPeriod updatedReviewPeriod = reviewPeriodServices.update(reviewPeriod);
        return HttpResponse.ok(updatedReviewPeriod)
                        .headers(headers -> headers
                                .location(URI.create(String.format("%s/%s", request.getPath(), updatedReviewPeriod.getId())))
                        );
    }

    /**
     * Delete a {@link ReviewPeriod}.
     *
     * @param id  the id of the review period to be deleted to delete
     */
    @Delete("/{id}")
    @RequiredPermission(Permission.CAN_DELETE_REVIEW_PERIOD)
    @Status(HttpStatus.OK)
    public void deleteReviewPeriod(@NotNull UUID id) {
        reviewPeriodServices.delete(id);
    }
}