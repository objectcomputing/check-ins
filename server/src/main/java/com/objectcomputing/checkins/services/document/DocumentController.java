package com.objectcomputing.checkins.services.document;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/documents")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "documents")
public class DocumentController {

    private final DocumentServices documentServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public DocumentController(DocumentServices documentServices,
                              EventLoopGroup eventLoopGroup,
                              @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.documentServices = documentServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<Document>> create(@Body @Valid Document document) {
        return Mono.fromCallable(() -> documentServices.save(document))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(doc -> (HttpResponse<Document>) HttpResponse.created(doc))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Get("/{id}")
    public Mono<HttpResponse<Document>> getById(@NotNull UUID id) {
        return Mono.fromCallable(() -> documentServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(doc -> (HttpResponse<Document>) HttpResponse.ok(doc))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Put
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public Mono<HttpResponse<Document>> update(@Body @Valid @NotNull Document document) {
        return Mono.fromCallable(() -> documentServices.update(document))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(doc -> (HttpResponse<Document>) HttpResponse.ok(doc))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Delete("/{id}")
    @Secured(RoleType.Constants.ADMIN_ROLE)
    public HttpResponse<?> delete(@NotNull UUID id) {
        documentServices.delete(id);
        return HttpResponse.noContent();
    }

}
