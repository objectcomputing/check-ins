package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/questions-and-answers")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class QuestionAndAnswerController {

    private final QuestionAndAnswerServices questionAndAnswerServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public QuestionAndAnswerController(QuestionAndAnswerServices questionAndAnswerServices,
                                    EventLoopGroup eventLoopGroup,
                                    @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.questionAndAnswerServices = questionAndAnswerServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    @Get("/{?requestId,questionId}")
    public Mono<HttpResponse<QuestionAndAnswerServices.Tuple>> getQuestionAndAnswer(@Nullable UUID requestId, @Nullable UUID questionId) {
        return Mono.fromCallable(() -> questionAndAnswerServices.getQuestionAndAnswer(requestId, questionId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(pair -> (HttpResponse<QuestionAndAnswerServices.Tuple>) HttpResponse.ok(pair))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }

    @Get("/{requestId}")
    public Mono<HttpResponse<List<QuestionAndAnswerServices.Tuple>>> getAllQuestionsAndAnswers(@Nullable UUID requestId) {
        return Mono.fromCallable(() -> questionAndAnswerServices.getAllQuestionsAndAnswers(requestId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(answerPairs -> (HttpResponse<List<QuestionAndAnswerServices.Tuple>>) HttpResponse.ok(answerPairs))
                .subscribeOn(Schedulers.fromExecutor(executorService));
    }
}
