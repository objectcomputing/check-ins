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
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/review-assignments")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "reviews")
public class ReviewAssignmentController {

    private final ReviewAssignmentServices reviewAssignmentServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public ReviewAssignmentController(ReviewAssignmentServices reviewAssignmentServices, EventLoopGroup eventLoopGroup,  @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.reviewAssignmentServices = reviewAssignmentServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new {@link ReviewAssignment}.
     *
     * @param assignment  a {@link ReviewAssignmentDTO} representing the desired review assignment
     * @return a streamable response containing the stored {@link ReviewAssignment}
     */
    @Post
    @RequiredPermission(Permission.CAN_CREATE_REVIEW_ASSIGNMENTS)
    public Mono<HttpResponse<ReviewAssignment>> createReviewAssignment(@Body @Valid ReviewAssignmentDTO assignment, HttpRequest<ReviewAssignmentDTO> request) {

        return Mono.fromCallable(() -> reviewAssignmentServices.save(assignment.convertToEntity()))
            .publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(reviewAssignment -> (HttpResponse<ReviewAssignment>) HttpResponse.created(reviewAssignment)
                .headers(headers -> headers.location(
                    URI.create(String.format("%s/%s", request.getPath(), reviewAssignment.getId())))))
            .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

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
    public Mono<HttpResponse<List<ReviewAssignment>>> createReviewAssignment(@NotNull UUID reviewPeriodId, @Body @Valid List<ReviewAssignmentDTO> assignments) {

        return Mono.fromCallable(() -> reviewAssignmentServices.saveAll(reviewPeriodId,
                assignments.stream().map(ReviewAssignmentDTO::convertToEntity).collect(Collectors.toList()),
                Boolean.TRUE))
            .publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(reviewAssignments -> (HttpResponse<List<ReviewAssignment>>) HttpResponse.created(reviewAssignments))
            .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

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
        }).publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(reviewAssignment -> (HttpResponse<ReviewAssignment>) HttpResponse.ok(reviewAssignment))
            .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @RequiredPermission(Permission.CAN_VIEW_REVIEW_ASSIGNMENTS)
    @Get("/period/{reviewPeriodId}{?reviewerId}")
    public Mono<HttpResponse<Set<ReviewAssignment>>> findAssignmentsByPeriodId(@NotNull UUID reviewPeriodId, @Nullable UUID reviewerId) {

        return Mono.fromCallable(() -> reviewAssignmentServices.findAllByReviewPeriodIdAndReviewerId(reviewPeriodId, reviewerId))
            .publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(assignments -> (HttpResponse<Set<ReviewAssignment>>) HttpResponse.ok(assignments))
            .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update an existing {@link ReviewAssignment}.
     *
     * @param reviewAssignment  the updated {@link ReviewAssignment}
     * @return a streamable response containing the stored {@link ReviewAssignment}
     */
    @RequiredPermission(Permission.CAN_UPDATE_REVIEW_ASSIGNMENTS)
    @Put
    public Mono<HttpResponse<ReviewAssignment>> update(@Body @Valid ReviewAssignment reviewAssignment, HttpRequest<ReviewAssignment> request) {

        return Mono.fromCallable(() -> reviewAssignmentServices.update(reviewAssignment))
            .publishOn(Schedulers.fromExecutor(eventLoopGroup))
            .map(updatedReviewAssignment -> (HttpResponse<ReviewAssignment>) HttpResponse
                .ok()
                .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedReviewAssignment.getId()))))
                .body(updatedReviewAssignment))
            .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Delete a {@link ReviewAssignment}.
     *
     * @param id  the id of the review assignment to be deleted to delete
     */
    @RequiredPermission(Permission.CAN_DELETE_REVIEW_ASSIGNMENTS)
    @Delete("/{id}")
    public HttpResponse<?> deleteReviewAssignment(@NotNull UUID id) {
        reviewAssignmentServices.delete(id);
        return HttpResponse
            .ok();
    }

}
