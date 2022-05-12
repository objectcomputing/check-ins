package com.objectcomputing.checkins.services.feedback;

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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/feedback")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback")
public class FeedbackController {
    private final FeedbackServices feedbackServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;
    private final CurrentUserServices currentUserServices;

    public FeedbackController(FeedbackServices feedbackServices,
                              EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService executorService,
                              CurrentUserServices currentUserServices) {
        this.feedbackServices = feedbackServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
        this.currentUserServices = currentUserServices;
    }

    /**
     * Create a feedback
     *
     * @param requestBody {@link FeedbackCreateDTO} New feedback to create
     * @return {@link FeedbackResponseDTO}
     */
    @Post()
    public Mono<HttpResponse<FeedbackResponseDTO>> save(@Body @Valid @NotNull FeedbackCreateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackServices.save(fromDTO(requestBody)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedFeedback -> (HttpResponse<FeedbackResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedback))
                        .headers(headers -> headers.location(URI.create("/feedback/" + savedFeedback.getId()))))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Update a feedback
     *
     * @param requestBody {@link FeedbackUpdateDTO} The updated feedback
     * @return {@link FeedbackResponseDTO}
     */
    @Put()
    public Mono<HttpResponse<FeedbackResponseDTO>> update(@Body @Valid @NotNull FeedbackUpdateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackServices.update(fromDTO(requestBody)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedFeedback -> (HttpResponse<FeedbackResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback/" + savedFeedback.getId())))
                        .body(fromEntity(savedFeedback)))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Delete a feedback
     *
     * @param id {@link UUID} ID of the feedback being deleted
     * @return
     */
    @Delete("/{id}")
    public Mono<HttpResponse> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> feedbackServices.delete(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Get feedback by ID
     *
     * @param id {@link UUID} ID of the requested feedback
     * @return {@link FeedbackResponseDTO}
     */
    @Get("/{id}")
    public Mono<HttpResponse<FeedbackResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> feedbackServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(feedback -> (HttpResponse<FeedbackResponseDTO>) HttpResponse
                        .ok(fromEntity(feedback))
                        .headers(headers -> headers.location(URI.create("/feedback/" + feedback.getId()))))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Get feedbacks by creator's ID, receiver's ID, confidentiality, or get all if no input provided
     *
     * @param sentBy {@link UUID} ID of member profile who created the feedbacks
     * @param sentTo {@link UUID} ID of member profile who received the feedbacks
     * @param confidential {@link Boolean} True for private feedbacks, else false
     * @return {@link List<FeedbackResponseDTO>} List of feedbacks that match the input parameters
     */
    @Get("/{?sentBy,sentTo,confidential}")
    public Mono<HttpResponse<List<FeedbackResponseDTO>>> getByValues(@Nullable UUID sentBy,
                                                                       @Nullable UUID sentTo,
                                                                       @Nullable Boolean confidential) {
        return Mono.fromCallable(() -> feedbackServices.getByValues(sentBy, sentTo, confidential))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(feedbacks -> {
                    List<FeedbackResponseDTO> dtoList = feedbacks.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.fromExecutor(executorService));
    }

    private FeedbackResponseDTO fromEntity(Feedback feedback) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setContent(feedback.getContent());
        dto.setSentTo(feedback.getSentTo());
        dto.setSentBy(feedback.getSentBy());
        dto.setConfidential(feedback.getConfidential());
        dto.setCreatedOn(feedback.getCreatedOn());
        dto.setUpdatedOn(feedback.getUpdatedOn());
        return dto;
    }

    private Feedback fromDTO(FeedbackCreateDTO dto) {
        return new Feedback(dto.getContent(), dto.getSentTo(), currentUserServices.getCurrentUser().getId(),
                dto.getConfidential());
    }

    private Feedback fromDTO(FeedbackUpdateDTO dto) {
        return new Feedback(dto.getId(), dto.getContent(),
                dto.getConfidential());
    }
}
