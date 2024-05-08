package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.NotFoundException;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/review-periods")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "reviews")
public class ReviewPeriodController {

    private final ReviewPeriodServices reviewPeriodServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public ReviewPeriodController(ReviewPeriodServices reviewPeriodServices, EventLoopGroup eventLoopGroup, @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.reviewPeriodServices = reviewPeriodServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new {@link ReviewPeriod}.
     *
     * @param period  a {@link ReviewPeriodCreateDTO} representing the desired review period
     * @return a streamable response containing the stored {@link ReviewPeriod}
     */
    @Post()
    public Mono<HttpResponse<ReviewPeriod>> createReviewPeriod(@Body @Valid ReviewPeriodCreateDTO period, HttpRequest<?> request) {

        return Mono.fromCallable(() -> reviewPeriodServices.save(period.convertToEntity()))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(reviewPeriod -> (HttpResponse<ReviewPeriod>) HttpResponse.created(reviewPeriod)
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), reviewPeriod.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Retrieve a {@link ReviewPeriod} given its id.
     *
     * @param id {@link UUID} of the review entry
     * @return a streamable response containing the found {@link ReviewPeriod} with the given ID
     */

    @Get("/{id}")
    public Mono<HttpResponse<ReviewPeriod>> getById(@NotNull UUID id) {

        return Mono.fromCallable(() -> {
            ReviewPeriod result = reviewPeriodServices.findById(id);
            if (result == null) {
                throw new NotFoundException("No review period for UUID");
            }
            return result;
        }).publishOn(Schedulers.fromExecutor(eventLoopGroup)).map(reviewPeriod -> {
            return (HttpResponse<ReviewPeriod>) HttpResponse.ok(reviewPeriod);
        }).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find {@link ReviewPeriod}s by name and/or open status, if both are blank get all review periods.
     *
     * @param name, name of the review period
     * @param reviewStatus, the current {@link ReviewStatus} of the review (
     * @return a streamable response containing a {@link Set} of {@link ReviewPeriod}s that match the given criteria
     */

    @Get("/{?name,reviewStatus}")
    public Mono<HttpResponse<Set<ReviewPeriod>>> findByValue(@Nullable String name,
                                                      @Nullable ReviewStatus reviewStatus) {

        return Mono.fromCallable(() -> reviewPeriodServices.findByValue(name, reviewStatus))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(reviewPeriods -> (HttpResponse<Set<ReviewPeriod>>) HttpResponse.ok(reviewPeriods))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update an existing {@link ReviewPeriod}.
     *
     * @param reviewPeriod  the updated {@link ReviewPeriod}
     * @return a streamable response containing the stored {@link ReviewPeriod}
     */
    @Put()
    public Mono<HttpResponse<ReviewPeriod>> update(@Body @Valid ReviewPeriod reviewPeriod, HttpRequest<?> request) {

        return Mono.fromCallable(() -> reviewPeriodServices.update(reviewPeriod))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedReviewPeriod -> (HttpResponse<ReviewPeriod>) HttpResponse
                        .ok(updatedReviewPeriod)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedReviewPeriod.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Delete a {@link ReviewPeriod}.
     *
     * @param id  the id of the review period to be deleted to delete
     */
    @Delete("/{id}")
    public HttpResponse<?> deleteReviewPeriod(@NotNull UUID id) {
        reviewPeriodServices.delete(id);
        return HttpResponse
                .ok();
    }

}