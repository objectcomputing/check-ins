package com.objectcomputing.checkins.services.feedback.suggestions;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/suggestions")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback")
public class FeedbackSuggestionsController {

    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;
    private FeedbackSuggestionsService suggestionsService;

    public FeedbackSuggestionsController(FeedbackSuggestionsService suggestionsService,
                                         EventLoopGroup eventLoopGroup,
                                         @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.suggestionsService = suggestionsService;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    @Get("/{id}")
    public Mono<HttpResponse<List<FeedbackSuggestionDTO>>> getSuggestionsByProfileId(UUID id) {
        return Mono.fromCallable(() -> suggestionsService.getSuggestionsByProfileId(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(feedbacks -> (HttpResponse<List<FeedbackSuggestionDTO>>) HttpResponse.ok(feedbacks))
            .subscribeOn(Schedulers.fromExecutor(executorService));
    }
}
