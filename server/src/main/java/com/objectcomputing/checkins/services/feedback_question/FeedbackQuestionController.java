package com.objectcomputing.checkins.services.feedback_question;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/questions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback_questions")
public class FeedbackQuestionController {

    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FeedbackQuestionController(EventLoopGroup eventLoopGroup,
                                      ExecutorService executorService) {
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
        return null;
    }

    /**
     * Get feedback question by ID
     *
     * @param id The {@link UUID} of the feedback question
     * @return {@link FeedbackQuestionResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackQuestionResponseDTO>> getById(UUID id) {
        return null;
    }

    // TODO: Create endpoint for getting all questions for a given template ID and feedback request ID
}
