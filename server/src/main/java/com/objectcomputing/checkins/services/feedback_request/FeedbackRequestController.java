package com.objectcomputing.checkins.services.feedback_request;
import com.objectcomputing.checkins.services.feedback.FeedbackResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.http.HttpResponse;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/feedback/requests")
@Secured(SecurityRule.IS_AUTHENTICATED)

@Tag(name = "feedback request")
public class FeedbackRequestController {
    private final FeedbackRequestServices feedbackReqServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FeedbackRequestController(FeedbackRequestServices feedbackReqServices,
                                     EventLoopGroup eventLoopGroup,
                                     @Named(TaskExecutors.IO) ExecutorService executorService,
                                     CurrentUserServices currentUserServices) {
        this.feedbackReqServices = feedbackReqServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback request
     *
     * @param requestBody {@link FeedbackRequestCreateDTO} New feedback request to create
     * @return {@link FeedbackResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FeedbackRequestResponseDTO>> save(@Body @Valid @NotNull FeedbackRequestCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackReqServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackRequest -> (HttpResponse<FeedbackRequestResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackRequest))
                        .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedbackRequest.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback request
     *
     * @param requestBody {@link FeedbackRequestUpdateDTO} The updated feedback request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Put()
    public Single<HttpResponse<FeedbackRequestResponseDTO>> update(@Body @Valid @NotNull FeedbackRequestUpdateDTO requestBody) {
        return Single.fromCallable(() -> feedbackReqServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedback -> (HttpResponse<FeedbackRequestResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedback.getId())))
                        .body(fromEntity(savedFeedback)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback request by UUID
     *
     * @param id {@link UUID} of the feedback request to be deleted
     * @return {@link HttpResponse}
     */
    @Delete("/{id}")
    public Single<HttpResponse> delete(@NotNull UUID id) {
        return Single.fromCallable(() -> feedbackReqServices.delete(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(successFlag -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback requst by ID
     *
     * @param id {@link UUID} ID of the request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackRequestResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> feedbackReqServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackRequest -> (HttpResponse<FeedbackRequestResponseDTO>) HttpResponse
                        .ok(fromEntity(savedFeedbackRequest))
                        .headers(headers -> headers.location(URI.create("/feedback_request" + savedFeedbackRequest.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }
    /**
     * Get feedback request by creator's ID
     *
     * @param creatorId {@link UUID} ID of member profile who created the feedback request
     * @return {@link List<FeedbackResponseDTO>} List of feedback requests that were made by certain creator
     */
    @Get("/{?creatorId}")
    public Single<HttpResponse<List<FeedbackRequestResponseDTO>>> findByValue(@Nullable UUID creatorId) {
        return Single.fromCallable(() -> feedbackReqServices.findByValue(creatorId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackReqs -> {
                    List<FeedbackRequestResponseDTO> dtoList = feedbackReqs.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackRequestResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }

    private FeedbackRequestResponseDTO fromEntity(FeedbackRequest feedbackRequest) {
        FeedbackRequestResponseDTO dto = new FeedbackRequestResponseDTO();
        dto.setId(feedbackRequest.getId());
        dto.setCreatorId(feedbackRequest.getCreatorId());
        dto.setRequesteeId(feedbackRequest.getRequesteeId());
        dto.setRecipientId(feedbackRequest.getRecipientId());
        dto.setTemplateId(feedbackRequest.getTemplateId());
        dto.setSendDate(feedbackRequest.getSendDate());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());
        dto.setSubmitDate(feedbackRequest.getSubmitDate());
        dto.setSentiment(feedbackRequest.getSentiment());

        return dto;
    }

    private FeedbackRequest fromDTO(FeedbackRequestCreateDTO dto) {
        return new FeedbackRequest(
                dto.getCreatorId(),
                dto.getRequesteeId(),
                dto.getRecipientId(),
                dto.getTemplateId(),
                dto.getSendDate(),
                dto.getDueDate(),
                dto.getStatus(),
                dto.getSubmitDate(),
                dto.getSentiment());
    }

    private FeedbackRequest fromDTO(FeedbackRequestUpdateDTO dto) {
        return new FeedbackRequest(dto.getId(), dto.getDueDate(), dto.getStatus(), dto.getSubmitDate(), dto.getSentiment());
    }
}
