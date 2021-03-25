package com.objectcomputing.checkins.services.feedback;

import io.micronaut.http.HttpRequest;
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

import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback")
public class FeedbackController {
    private final FeedbackServices feedbackServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FeedbackController(FeedbackServices feedbackServices,
                              EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.feedbackServices = feedbackServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback
     *
     * @param
     * @return
     */
    @Post()
    public Single<HttpResponse<FeedbackResponseDTO>> save(@Body @Valid @NotNull FeedbackCreateDTO requestBody,
                                                          HttpRequest<FeedbackCreateDTO> request) {
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
     * @param
     * @return
     *
     */
    @Put()
    public Single<HttpResponse<FeedbackResponseDTO>> update(@Body @Valid @NotNull FeedbackUpdateDTO requestBody,
                                                            HttpRequest<FeedbackUpdateDTO> request) {
        return null;
    }

    @Delete("/{id}")
    public void delete(UUID id) {

    }

    @Get("/{id}")
    public void read(UUID id) {

    }

    private FeedbackResponseDTO fromEntity(Feedback feedback) {
        FeedbackResponseDTO dto = new FeedbackResponseDTO();
        dto.setId(feedback.getId());
        dto.setContent(feedback.getContent());
        dto.setSendTo(feedback.getSendTo());
        dto.setSendBy(feedback.getSendBy());
        dto.setConfidential(feedback.getConfidential());
        dto.setCreatedOn(feedback.getCreatedOn());
        dto.setUpdatedOn(feedback.getUpdatedOn());
        return dto;
    }

    private Feedback fromDTO(FeedbackCreateDTO dto) {
        return new Feedback(dto.getContent(), dto.getSendTo(), dto.getSendBy(),
                dto.getConfidential(), dto.getCreatedOn(), dto.getUpdatedOn());
    }
}
