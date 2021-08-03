package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Named;
import java.net.URI;
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
    public Single<HttpResponse<QuestionAndAnswerServices.Tuple>> getQuestionAndAnswer(@Nullable UUID requestId, @Nullable UUID questionId) {
        return Single.fromCallable(() -> questionAndAnswerServices.getQuestionAndAnswer(requestId, questionId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(pair -> (HttpResponse<QuestionAndAnswerServices.Tuple>) HttpResponse
                        .created((pair))
                        .headers(headers -> headers.location(URI.create("/feedback-pair/"))))
                .subscribeOn(Schedulers.from(executorService));
    }
}
