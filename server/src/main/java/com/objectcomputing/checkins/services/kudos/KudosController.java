package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/kudos")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "kudos")
public class KudosController {

    private final KudosServices kudosServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    private static final Logger LOG = LoggerFactory.getLogger(KudosController.class);

    public KudosController(KudosServices kudosServices,
                           EventLoopGroup eventLoopGroup,
                           @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.kudosServices = kudosServices;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    @Post()
    public Mono<HttpResponse<Kudos>> create(@Body @Valid KudosCreateDTO kudos, HttpRequest<KudosCreateDTO> request) {
        return Mono.fromCallable(() -> kudosServices.save(kudos))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(createdKudos -> (HttpResponse<Kudos>) HttpResponse
                        .created(createdKudos)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdKudos.getId()))))
                ).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Put()
    public Mono<HttpResponse<Kudos>> update(@Body @Valid KudosUpdateDTO kudos, HttpRequest<KudosUpdateDTO> request) {
        return Mono.fromCallable(() -> kudosServices.update(kudos))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(updatedKudos -> (HttpResponse<Kudos>) HttpResponse
                        .ok(updatedKudos)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), updatedKudos.getId()))))
                ).subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Get("/{id}")
    public Mono<HttpResponse<KudosResponseDTO>> getById(@NotNull UUID id) {
        return Mono.fromCallable(() -> kudosServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(kudos -> (HttpResponse<KudosResponseDTO>) HttpResponse.ok(kudos))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Get("/{?recipientId,?senderId,?isPending,?Public}")
    public Mono<HttpResponse<List<KudosResponseDTO>>> get(@Nullable UUID recipientId, @Nullable UUID senderId, @Nullable Boolean isPending, @Nullable Boolean Public) {
        return Mono.fromCallable(() -> kudosServices.findByValues(recipientId, senderId, isPending, Public))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(kudosList -> (HttpResponse<List<KudosResponseDTO>>) HttpResponse.ok(kudosList))
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> kudosServices.delete(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(deleted -> (HttpResponse<?>) HttpResponse.noContent())
                .subscribeOn(Schedulers.fromExecutor(ioExecutorService));
    }

}
