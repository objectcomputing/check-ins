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
import java.util.Set;
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
    private final CurrentUserServices currentUserServices;

    public FeedbackRequestController(FeedbackRequestServices feedbackReqServices,
                                     EventLoopGroup eventLoopGroup,
                                     @Named(TaskExecutors.IO) ExecutorService executorService,
                                     CurrentUserServices currentUserServices) {
        this.feedbackReqServices = feedbackReqServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
        this.currentUserServices = currentUserServices;
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
                        .headers(headers -> headers.location(URI.create("/feedback_request" + savedFeedbackRequest.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }


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
     * @return {@link Set <FeedbackResponseDTO>} Set of feedback requests that were made by certain creator
     */
    @Get("/{?creatorId}")
    public Single<HttpResponse<Set<FeedbackRequestResponseDTO>>> findByValue(@Nullable UUID creatorId) {
        return Single.fromCallable(() -> feedbackReqServices.findByValue(creatorId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackReqs -> {
                    Set<FeedbackRequestResponseDTO> dtoList = feedbackReqs.stream()
                            .map(this::fromEntity).collect(Collectors.toSet());
                    return (HttpResponse<Set<FeedbackRequestResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }

    private FeedbackRequestResponseDTO fromEntity(FeedbackRequest feedbackRequest) {
        FeedbackRequestResponseDTO dto = new FeedbackRequestResponseDTO();
        dto.setId(feedbackRequest.getId());
        dto.setRequesteeId(feedbackRequest.getRequesteeId());
        dto.setCreatorId(feedbackRequest.getCreatorId());
        dto.setTemplateId(feedbackRequest.getTemplateId());
        dto.setSendDate(feedbackRequest.getSendDate());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());

        return dto;
    }

    private FeedbackRequest fromDTO(FeedbackRequestCreateDTO dto) {
        return new FeedbackRequest(dto.getCreatorId(), dto.getRequesteeId(), dto.getTemplateId(), dto.getSendDate(), dto.getDueDate(), dto.getStatus());
    }
}
