package com.objectcomputing.checkins.services.feedback_question;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/questions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback_questions")
public class FeedbackQuestionController {

    private final FeedbackQuestionServices feedbackQuestionServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FeedbackQuestionController(FeedbackQuestionServices feedbackQuestionServices,
                                      EventLoopGroup eventLoopGroup,
                                      ExecutorService executorService) {
        this.feedbackQuestionServices = feedbackQuestionServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback question
     *
     * @param requestBody {@link FeedbackQuestionCreateDTO} The feedback question to create
     * @return {@link FeedbackQuestionResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FeedbackQuestionResponseDTO>> save(@Body @Valid @NotNull FeedbackQuestionCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackQuestionServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackQuestion -> (HttpResponse<FeedbackQuestionResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/feedback_questions/" + savedFeedbackQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback question by ID
     *
     * @param id The {@link UUID} of the feedback question
     * @return {@link FeedbackQuestionResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackQuestionResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> feedbackQuestionServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackQuestion -> (HttpResponse<FeedbackQuestionResponseDTO>) HttpResponse
                        .ok(fromEntity(feedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/feedback_questions/" + feedbackQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    // TODO: Create endpoint for getting all questions for a given template ID and feedback request ID

    private FeedbackQuestionResponseDTO fromEntity(FeedbackQuestion feedbackQuestion) {
        FeedbackQuestionResponseDTO dto = new FeedbackQuestionResponseDTO();
        dto.setId(feedbackQuestion.getId());
        dto.setQuestion(feedbackQuestion.getQuestion());
        dto.setTemplateId(feedbackQuestion.getTemplateId());
        return dto;
    }

    private FeedbackQuestion fromDTO(FeedbackQuestionCreateDTO dto) {
        return new FeedbackQuestion(dto.getQuestion(), dto.getTemplateId());
    }
}
