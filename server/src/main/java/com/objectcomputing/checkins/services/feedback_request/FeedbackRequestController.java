package com.objectcomputing.checkins.services.feedback_request;
import com.objectcomputing.checkins.services.feedback.FeedbackResponseDTO;
import io.micronaut.core.convert.format.Format;
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
import java.time.LocalDate;
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
                                     @Named(TaskExecutors.IO) ExecutorService executorService) {
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
    public HttpResponse<?> delete(@NotNull UUID id) {
        feedbackReqServices.delete(id);
        return HttpResponse.ok();
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
     * Search for all feedback requests that match the intersection of the provided values
     * Any values that are null are not applied to the intersection
     *
     * @param creatorId The {@link UUID} of the creator of the request
     * @param requesteeId The {@link UUID} of the requestee
     * @param templateId The {@link UUID} of the feedback template
     * @param oldestDate The date that filters out any requests that were made before that date
     * @return list of {@link FeedbackRequestResponseDTO}
     */
    @Get("/{?creatorId,requesteeId,templateId,oldestDate}")
    public Single<HttpResponse<List<FeedbackRequestResponseDTO>>> findByValues(@Nullable UUID creatorId, @Nullable UUID requesteeId, @Nullable UUID templateId, @Nullable @Format("yyyy-MM-dd") LocalDate oldestDate) {
        return Single.fromCallable(() -> feedbackReqServices.findByValues(creatorId, requesteeId, templateId, oldestDate))
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
                dto.getSubmitDate());
    }

    private FeedbackRequest fromDTO(FeedbackRequestUpdateDTO dto) {
        return new FeedbackRequest(dto.getId(), dto.getDueDate(), dto.getStatus(), dto.getSubmitDate());
    }
}
