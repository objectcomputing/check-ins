package com.objectcomputing.checkins.services.feedback_request;
import com.objectcomputing.checkins.security.permissions.Permissions;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Validated
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
     * @return {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permissions.CAN_CREATE_FEEDBACK_REQUEST)
    @Post()
    public Mono<HttpResponse<FeedbackRequestResponseDTO>> save(@Body @Valid @NotNull FeedbackRequestCreateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackReqServices.save(fromDTO(requestBody)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedFeedbackRequest -> (HttpResponse<FeedbackRequestResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackRequest))
                        .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedbackRequest.getId()))))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Update a feedback request
     *
     * @param requestBody {@link FeedbackRequestUpdateDTO} The updated feedback request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Put()
    public Mono<HttpResponse<FeedbackRequestResponseDTO>> update(@Body @Valid @NotNull FeedbackRequestUpdateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackReqServices.update(requestBody))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedFeedback -> (HttpResponse<FeedbackRequestResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedback.getId())))
                        .body(fromEntity(savedFeedback)))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Delete a feedback request by UUID
     *
     * @param id {@link UUID} of the feedback request to be deleted
     * @return {@link HttpResponse}
     */
    @RequiredPermission(Permissions.CAN_DELETE_FEEDBACK_REQUEST)
    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> feedbackReqServices.delete(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Get feedback requst by ID
     *
     * @param id {@link UUID} ID of the request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permissions.CAN_VIEW_FEEDBACK_REQUEST)
    @Get("/{id}")
    public Mono<HttpResponse<FeedbackRequestResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> feedbackReqServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedFeedbackRequest -> (HttpResponse<FeedbackRequestResponseDTO>) HttpResponse
                        .ok(fromEntity(savedFeedbackRequest))
                        .headers(headers -> headers.location(URI.create("/feedback_request" + savedFeedbackRequest.getId()))))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    /**
     * Search for all feedback requests that match the intersection of the provided values
     * Any values that are null are not applied to the intersection
     *
     * @param creatorId The {@link UUID} of the creator of the request
     * @param requesteeId The {@link UUID} of the requestee
     * @param recipientId The {@link UUID} of the recipient
     * @param oldestDate The date that filters out any requests that were made before that date
     * @return list of {@link FeedbackRequestResponseDTO}
     */
    @RequiredPermission(Permissions.CAN_VIEW_FEEDBACK_REQUEST)
    @Get("/{?creatorId,requesteeId,recipientId,oldestDate}")
    public Mono<HttpResponse<List<FeedbackRequestResponseDTO>>> findByValues(@Nullable UUID creatorId, @Nullable UUID requesteeId, @Nullable UUID recipientId, @Nullable @Format("yyyy-MM-dd") LocalDate oldestDate) {
        return Mono.fromCallable(() -> feedbackReqServices.findByValues(creatorId, requesteeId, recipientId, oldestDate))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(feedbackReqs -> {
                    List<FeedbackRequestResponseDTO> dtoList = feedbackReqs.stream()
                            .map(FeedbackRequestController::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackRequestResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.fromExecutor(executorService));
    }

    private static FeedbackRequestResponseDTO fromEntity(FeedbackRequest feedbackRequest) {
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

}
