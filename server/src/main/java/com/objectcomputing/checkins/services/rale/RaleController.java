package com.objectcomputing.checkins.services.rale;


import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.micronaut.core.annotation.Nullable;
import javax.inject.Named;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/rales")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "rales")
public class RaleController {

    private final RaleServices raleService;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService ioExecutorService;

    public RaleController(RaleServices raleService,
                          EventLoopGroup eventLoopGroup,
                          @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.raleService = raleService;
        this.eventLoopGroup = eventLoopGroup;
        this.ioExecutorService = ioExecutorService;
    }

    /**
     * Create and save a new rale
     *
     * @param rale, {@link RaleCreateDTO}
     * @return {@link HttpResponse<RaleResponseDTO>}
     */
    @Post()
    public Single<HttpResponse<RaleResponseDTO>> createARale(@Body @Valid RaleCreateDTO rale, HttpRequest<RaleCreateDTO> request) {

        return Single.fromCallable(() -> raleService.save(rale))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(createdRale -> (HttpResponse<RaleResponseDTO>) HttpResponse
                        .created(createdRale)
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getPath(), createdRale.getId())))))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Get rale based on id
     *
     * @param id of rale
     * @return {@link RaleResponseDTO rale matching id}
     */

    @Get("/{id}")
    public Single<HttpResponse<RaleResponseDTO>> readRale(@NotNull UUID id) {
        return Single.fromCallable(() -> raleService.read(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(rale -> (HttpResponse<RaleResponseDTO>) HttpResponse.ok(rale))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Find rale(s) given a combination of the following parameters
     *
     * @param rale,     name of the rale
     * @param memberId, {@link UUID} of the member you wish to inquire in to which rales they are a part of
     * @return {@link List<RaleResponseDTO> list of rales}, return all rales when no parameters filled in else
     * return all rales that match all of the filled in params
     */
    @Get("/{?rale,memberId}")
    public Single<HttpResponse<Set<RaleResponseDTO>>> findRales(@Nullable RaleType rale, @Nullable UUID memberId) {
        return Single.fromCallable(() -> raleService.findByFields(rale, memberId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(rales -> (HttpResponse<Set<RaleResponseDTO>>) HttpResponse.ok(rales))
                .subscribeOn(Schedulers.from(ioExecutorService));
    }

    /**
     * Update rale.
     *
     * @param rale, {@link RaleUpdateDTO}
     * @return {@link HttpResponse<RaleResponseDTO>}
     */
    @Put()
    public Single<HttpResponse<RaleResponseDTO>> update(@Body @Valid RaleUpdateDTO rale, HttpRequest<RaleUpdateDTO> request) {
        return Single.fromCallable(() -> raleService.update(rale))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(updated -> (HttpResponse<RaleResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create(String.format("%s/%s", request.getUri(), rale.getId()))))
                        .body(updated))
                .subscribeOn(Schedulers.from(ioExecutorService));

    }

    /**
     * Delete Rale
     *
     * @param id, id of {@link RaleUpdateDTO} to delete
     * @return
     */
    @Delete("/{id}")
    public Single<HttpResponse> deleteRale(@NotNull UUID id) {
        return Single.fromCallable(() -> raleService.delete(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(success -> (HttpResponse) HttpResponse.ok())
                .subscribeOn(Schedulers.from(ioExecutorService));
    }
}