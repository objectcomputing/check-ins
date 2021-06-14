package com.objectcomputing.checkins.services.feedback_request;
import com.objectcomputing.checkins.services.feedback.FeedbackResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.concurrent.ExecutorService;

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
