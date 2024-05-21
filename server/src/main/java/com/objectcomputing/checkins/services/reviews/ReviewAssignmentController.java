package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/review-assignments")
@ExecuteOn(TaskExecutors.IO)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "reviews")
public class ReviewAssignmentController {

    private final ReviewAssignmentServices reviewAssignmentServices;

    public ReviewAssignmentController(ReviewAssignmentServices reviewAssignmentServices) {
        this.reviewAssignmentServices = reviewAssignmentServices;
    }

    /**
     * Create and save a new {@link ReviewAssignment}.
     *
     * @param assignment  a {@link ReviewAssignmentDTO} representing the desired review assignment
     * @return a streamable response containing the stored {@link ReviewAssignment}
     */
    @Post
    @RequiredPermission(Permission.CAN_CREATE_REVIEW_ASSIGNMENTS)
    public Mono<HttpResponse<ReviewAssignment>> createReviewAssignment(@Body @Valid ReviewAssignmentDTO assignment, HttpRequest<?> request) {
        return Mono.fromCallable(() -> reviewAssignmentServices.save(assignment.convertToEntity()))
            .map(reviewAssignment -> HttpResponse.created(reviewAssignment)
                .headers(headers -> headers.location(
                    URI.create(String.format("%s/%s", request.getPath(), reviewAssignment.getId())))));

    }

    /**
     * Create and save multiple new {@link ReviewAssignment}s and associate them with a given {@link ReviewPeriod}.
     *
     * @param reviewPeriodId a {@link UUID} representing the review period to associate the review assignments with
     * @param assignments a List of {@link ReviewAssignmentDTO} representing the desired review assignments
     * @return a streamable response containing the list of stored {@link ReviewAssignment}
     */
    @Post("/{reviewPeriodId}")
    @RequiredPermission(Permission.CAN_CREATE_REVIEW_ASSIGNMENTS)
    public Mono<HttpResponse<List<ReviewAssignment>>> createReviewAssignment(@NotNull UUID reviewPeriodId,
                                                                             @Body List<@Valid ReviewAssignmentDTO> assignments) {
        return Mono.fromCallable(() -> reviewAssignmentServices.saveAll(reviewPeriodId,
                assignments.stream().map(ReviewAssignmentDTO::convertToEntity).collect(Collectors.toList()), Boolean.TRUE)
                ).map(HttpResponse::created);

    }

    /**
     * Retrieve a {@link ReviewAssignment} given its id.
     *
     * @param id {@link UUID} of the review assignment
     * @return a streamable response containing the found {@link ReviewAssignment} with the given ID
     */

    @RequiredPermission(Permission.CAN_VIEW_REVIEW_ASSIGNMENTS)
    @Get("/{id}")
    public Mono<HttpResponse<ReviewAssignment>> getById(@NotNull UUID id) {
        return Mono.fromCallable(() -> {
            ReviewAssignment result = reviewAssignmentServices.findById(id);
            if (result == null) {
                throw new NotFoundException("No review assignment for UUID");
            }
            return result;
        }).map(HttpResponse::ok);
    }

    @RequiredPermission(Permission.CAN_VIEW_REVIEW_ASSIGNMENTS)
    @Get("/period/{reviewPeriodId}{?reviewerId}")
    public Mono<HttpResponse<Set<ReviewAssignment>>> findAssignmentsByPeriodId(@NotNull UUID reviewPeriodId, @Nullable @QueryValue UUID reviewerId) {
        return Mono.fromCallable(() -> reviewAssignmentServices.findAllByReviewPeriodIdAndReviewerId(reviewPeriodId, reviewerId))
            .map(HttpResponse::ok);
    }

    /**
     * Update an existing {@link ReviewAssignment}.
     *
     * @param reviewAssignment  the updated {@link ReviewAssignment}
     * @return a streamable response containing the stored {@link ReviewAssignment}
     */
    @RequiredPermission(Permission.CAN_UPDATE_REVIEW_ASSIGNMENTS)
    @Put
    public Mono<HttpResponse<ReviewAssignment>> update(@Body @Valid ReviewAssignment reviewAssignment, HttpRequest<?> request) {
        return Mono.fromCallable(() -> reviewAssignmentServices.update(reviewAssignment))
            .map(updatedReviewAssignment -> HttpResponse.ok(updatedReviewAssignment)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedReviewAssignment.getId())))));
    }

    /**
     * Delete a {@link ReviewAssignment}.
     *
     * @param id  the id of the review assignment to be deleted to delete
     */
    @RequiredPermission(Permission.CAN_DELETE_REVIEW_ASSIGNMENTS)
    @Delete("/{id}")
    public Mono<HttpResponse<?>> deleteReviewAssignment(@NotNull UUID id) {
        return Mono.fromRunnable(() -> reviewAssignmentServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }

}
