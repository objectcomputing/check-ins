package com.objectcomputing.checkins.services.questions;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/questions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "questions")
public class QuestionController {

    private final QuestionServices questionService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public QuestionController(QuestionServices questionService, EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.questionService = questionService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new question.
     *
     * @param question, {@link QuestionCreateDTO}
     * @return {@link HttpResponse<QuestionResponseDTO>}
     */

    @Post()
    public Mono<HttpResponse<QuestionResponseDTO>> createAQuestion(@Body @Valid QuestionCreateDTO question, HttpRequest<QuestionCreateDTO> request) {

        return Mono.fromCallable(() -> questionService.saveQuestion(toModel(question)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(newQuestion -> (HttpResponse<QuestionResponseDTO>) HttpResponse
                        .created(fromModel(newQuestion))
                        .headers(headers -> headers.location(
                                URI.create(String.format("%s/%s", request.getPath(), newQuestion.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    /**
     * Find and read a question given its id.
     *
     * @param id {@link UUID} of the question entry
     * @return {@link HttpResponse< QuestionResponseDTO >}
     */
    @Get("/{id}")
    public Mono<HttpResponse<QuestionResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> {
            Question found = questionService.findById(id);
            if (found == null) {
                throw new NotFoundException("No question for UUID");
            }
            return found;
        })
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(question -> (HttpResponse<QuestionResponseDTO>) HttpResponse.ok(fromModel(question)))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Find questions with a particular string, a particular categoryId or read all questions.
     *
     * @param text, the text of the question
     * @param categoryId, the category id of the question
     * @return {@link List HttpResponse<QuestionResponseDTO >
     */
    @Get("/{?text,categoryId}")
    public Mono<HttpResponse<Set<QuestionResponseDTO>>> findByText(@Nullable String text, @Nullable UUID categoryId) {
        return Mono.fromCallable(() -> {
            if (text != null) {
                return questionService.findByText(text);
            } else if (categoryId != null) {
                return questionService.findByCategoryId(categoryId);
            } else {
                return questionService.readAllQuestions();
            }
        })
        .publishOn(Schedulers.fromExecutor(eventLoopGroup))
        .map(questions -> {
            Set<QuestionResponseDTO> responseBody = questions.stream()
                    .map(question -> fromModel(question))
                    .collect(Collectors.toSet());
            return (HttpResponse<Set<QuestionResponseDTO>>) HttpResponse.ok(responseBody);
        })
        .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    /**
     * Update the text of a question.
     *
     * @param question, {@link QuestionUpdateDTO}
     * @return {@link HttpResponse< QuestionResponseDTO >}
     */
    @Put()
    public Mono<HttpResponse<QuestionResponseDTO>> update(@Body @Valid QuestionUpdateDTO question, HttpRequest<QuestionCreateDTO> request) {
        if (question == null) {
            return Mono.just(HttpResponse.ok());
        }
        return Mono.fromCallable(() -> questionService.update(toModel(question)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedQuestion ->
                        (HttpResponse<QuestionResponseDTO>) HttpResponse
                                .created(fromModel(updatedQuestion))
                                .headers(headers -> headers.location(
                                        URI.create(String.format("%s/%s", request.getPath(), updatedQuestion.getId())))))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));

    }

    private QuestionResponseDTO fromModel(Question question) {
        QuestionResponseDTO qrdto = new QuestionResponseDTO();
        qrdto.setId(question.getId());
        qrdto.setText(question.getText());
        if (question.getCategoryId() != null) {
            qrdto.setCategoryId(question.getCategoryId());
        }
        return qrdto;
    }

    private Question toModel(QuestionUpdateDTO dto) {
        Question model = new Question();
        model.setId(dto.getId());
        model.setText(dto.getText());
        if (dto.getCategoryId() != null) {
            model.setCategoryId(dto.getCategoryId());
        }
        return model;
    }

    private Question toModel(QuestionCreateDTO dto) {
        Question model = new Question();
        model.setText(dto.getText());
        if (dto.getCategoryId() != null) {
            model.setCategoryId(dto.getCategoryId());
        }
        return model;
    }

}
