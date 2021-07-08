package com.objectcomputing.checkins.services.feedback_question;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateResponseDTO;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateUpdateDTO;
import com.objectcomputing.checkins.services.question_category.QuestionCategory;
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
@Tag(name = "template_questions")
public class TemplateQuestionController {

    private final TemplateQuestionServices templateQuestionServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public TemplateQuestionController(TemplateQuestionServices templateQuestionServices,
                                      EventLoopGroup eventLoopGroup,
                                      ExecutorService executorService) {
        this.templateQuestionServices = templateQuestionServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback question
     *
     * @param requestBody {@link TemplateQuestionCreateDTO} The feedback question to create
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Post()
    public Single<HttpResponse<TemplateQuestionResponseDTO>> save(@Body @Valid @NotNull TemplateQuestionCreateDTO requestBody) {
        return Single.fromCallable(() -> templateQuestionServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackQuestion -> (HttpResponse<TemplateQuestionResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback template question
     *
     * @param requestBody {@link TemplateQuestionUpdateDTO} The updated template question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Put()
    public Single<HttpResponse<TemplateQuestionResponseDTO>> update(@Body @Valid @NotNull TemplateQuestionUpdateDTO requestBody) {
        return Single.fromCallable(() -> templateQuestionServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackTemplateQ -> (HttpResponse<TemplateQuestionResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/template_questions/" + savedFeedbackTemplateQ.getId())))
                        .body(fromEntity(savedFeedbackTemplateQ)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback template question
     *
     * @param id {@link UUID} ID of the feedback template question being deleted
     *        @return {Boolean}
     */

    @Delete("/{id}")
    public HttpResponse<?> deleteTemplateQuestion(@NotNull UUID id) {
        templateQuestionServices.delete(id);
        return HttpResponse
                .ok();
    }


    /**
     * Get feedback question by ID
     *
     * @param id The {@link UUID} of the feedback question
     * @return {@link TemplateQuestionResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<TemplateQuestionResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> templateQuestionServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackQuestion -> (HttpResponse<TemplateQuestionResponseDTO>) HttpResponse
                        .ok(fromEntity(feedbackQuestion))
                        .headers(headers -> headers.location(URI.create("/template_questions/" + feedbackQuestion.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }



    // TODO: Create endpoint for getting all questions for a given template ID and feedback request ID

    private TemplateQuestionResponseDTO fromEntity(TemplateQuestion templateQuestion) {
        TemplateQuestionResponseDTO dto = new TemplateQuestionResponseDTO();
        dto.setId(templateQuestion.getId());
        dto.setQuestion(templateQuestion.getQuestion());
        dto.setTemplateId(templateQuestion.getTemplateId());
        dto.setOrderNum(templateQuestion.getOrderNum());
        return dto;
    }

    private TemplateQuestion fromDTO(TemplateQuestionCreateDTO dto) {
        return new TemplateQuestion(dto.getQuestion(), dto.getTemplateId(), dto.getOrderNum());
    }

    private TemplateQuestion fromDTO(TemplateQuestionUpdateDTO dto) {
        return new TemplateQuestion(dto.getQuestion(), dto.getTemplateId(), dto.getOrderNum());
    }
}
