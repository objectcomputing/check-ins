package com.objectcomputing.checkins.services.feedback;

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

import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import java.net.URI;
import java.time.LocalDateTime;
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
    public Single<HttpResponse<FeedbackResponseDTO>> save(@Body @Valid @NotNull FeedbackCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedback -> (HttpResponse<FeedbackResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedback))
                        .headers(headers -> headers.location(URI.create("/feedback/" + savedFeedback.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback
     *
     * @param requestBody {@link FeedbackUpdateDTO} The updated feedback
     * @return {@link FeedbackResponseDTO}
     */
    @Put()
    public Single<HttpResponse<FeedbackResponseDTO>> update(@Body @Valid @NotNull FeedbackUpdateDTO requestBody) {
        return Single.fromCallable(() -> feedbackServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedback -> (HttpResponse<FeedbackResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback/" + savedFeedback.getId())))
                        .body(fromEntity(savedFeedback)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback
     *
     * @param id {@link UUID} ID of the feedback being deleted
     * @return
     */
    @Delete("/{id}")
    public Single<HttpResponse> delete(@NotNull UUID id) {
        return Single.fromCallable(() -> feedbackServices.delete(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(successFlag -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback by ID
     *
     * @param id {@link UUID} ID of the requested feedback
     * @return {@link FeedbackResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> feedbackServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedback -> (HttpResponse<FeedbackResponseDTO>) HttpResponse
                        .ok(fromEntity(feedback))
                        .headers(headers -> headers.location(URI.create("/feedback/" + feedback.getId()))))
                .subscribeOn(Schedulers.from(executorService));
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
    public Single<HttpResponse<List<FeedbackResponseDTO>>> getByValues(@Nullable UUID sentBy,
                                                                       @Nullable UUID sentTo,
                                                                       @Nullable Boolean confidential) {
        return Single.fromCallable(() -> feedbackServices.getByValues(sentBy, sentTo, confidential))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbacks -> {
                    List<FeedbackResponseDTO> dtoList = feedbacks.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
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
