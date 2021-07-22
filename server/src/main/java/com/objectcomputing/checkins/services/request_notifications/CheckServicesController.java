package com.objectcomputing.checkins.services.request_notifications;


import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestCreateDTO;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestResponseDTO;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestUpdateDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import javax.annotation.security.PermitAll;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/feedback/daily-request-check")
@PermitAll
public class CheckServicesController {
    private final CheckServices checkServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public CheckServicesController(CheckServices checkServices, EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.checkServices = checkServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Get
    public Single<? extends HttpResponse<?>> GetTodaysRequests() {
        return Single.fromCallable(() -> checkServices.GetTodaysRequests())
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(Schedulers.from(ioExecutorService));

    }

}
