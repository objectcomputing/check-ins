package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
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
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.annotation.Status;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller("/services/review-assignments")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "reviews")
public class ReviewAssignmentController {

    private final ReviewAssignmentServices reviewAssignmentServices;

    public ReviewAssignmentController(ReviewAssignmentServices reviewAssignmentServices) {
        this.reviewAssignmentServices = reviewAssignmentServices;
    }

    /**
     * Create and save a new {@link ReviewAssignment}.
     *
     * @param assignment a {@link ReviewAssignmentDTO} representing the desired review assignment
     * @return a streamable response containing the stored {@link ReviewAssignment}
     */
    @Post
    @RequiredPermission(Permission.CAN_CREATE_REVIEW_ASSIGNMENTS)
    public HttpResponse<ReviewAssignment> createReviewAssignment(@Body @Valid ReviewAssignmentDTO assignment, HttpRequest<?> request) {
        ReviewAssignment reviewAssignment = reviewAssignmentServices.save(assignment.convertToEntity());
        return HttpResponse.created(reviewAssignment)
                .headers(headers -> headers.location(
                        URI.create(String.format("%s/%s", request.getPath(), reviewAssignment.getId()))));
    }

    /**
     * Create and save multiple new {@link ReviewAssignment}s and associate them with a given {@link ReviewPeriod}.
     *
     * @param reviewPeriodId a {@link UUID} representing the review period to associate the review assignments with
     * @param assignments    a List of {@link ReviewAssignmentDTO} representing the desired review assignments
     * @return a streamable response containing the list of stored {@link ReviewAssignment}
     */
    @Post("/{reviewPeriodId}")
    @RequiredPermission(Permission.CAN_CREATE_REVIEW_ASSIGNMENTS)
    @Status(HttpStatus.CREATED)
    public List<ReviewAssignment> createReviewAssignment(@NotNull UUID reviewPeriodId,
                                                         @Body List<@Valid ReviewAssignmentDTO> assignments) {
        List<ReviewAssignment> assignmentEntities = assignments.stream().map(ReviewAssignmentDTO::convertToEntity).toList();
        return reviewAssignmentServices.saveAll(reviewPeriodId, assignmentEntities, true);
    }

    /**
     * Retrieve a {@link ReviewAssignment} given its id.
     *
     * @param id {@link UUID} of the review assignment
     * @return a streamable response containing the found {@link ReviewAssignment} with the given ID
     */
    @RequiredPermission(Permission.CAN_VIEW_REVIEW_ASSIGNMENTS)
    @Get("/{id}")
    public ReviewAssignment getById(@NotNull UUID id) {
        ReviewAssignment result = reviewAssignmentServices.findById(id);
        if (result == null) {
            throw new NotFoundException("No review assignment for UUID");
        }
        return result;
    }

    @RequiredPermission(Permission.CAN_VIEW_REVIEW_ASSIGNMENTS)
    @Get("/period/{reviewPeriodId}{?reviewerId}")
    public Set<ReviewAssignment> findAssignmentsByPeriodId(@NotNull UUID reviewPeriodId, @Nullable @QueryValue UUID reviewerId) {
        return reviewAssignmentServices.findAllByReviewPeriodIdAndReviewerId(reviewPeriodId, reviewerId);
    }

    /**
     * Update an existing {@link ReviewAssignment}.
     *
     * @param reviewAssignment the updated {@link ReviewAssignment}
     * @return a streamable response containing the stored {@link ReviewAssignment}
     */
    @RequiredPermission(Permission.CAN_UPDATE_REVIEW_ASSIGNMENTS)
    @Put
    public HttpResponse<ReviewAssignment> update(@Body @Valid ReviewAssignment reviewAssignment, HttpRequest<?> request) {
        ReviewAssignment updatedReviewAssignment = reviewAssignmentServices.update(reviewAssignment);
        return HttpResponse.ok(updatedReviewAssignment)
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedReviewAssignment.getId()))));
    }

    /**
     * Delete a {@link ReviewAssignment}.
     *
     * @param id the id of the review assignment to be deleted to delete
     */
    @RequiredPermission(Permission.CAN_DELETE_REVIEW_ASSIGNMENTS)
    @Delete("/{id}")
    @Status(HttpStatus.OK)
    public void deleteReviewAssignment(@NotNull UUID id) {
        reviewAssignmentServices.delete(id);
    }
}
