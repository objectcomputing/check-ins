package com.objectcomputing.checkins.services.referraltype;

import io.micronaut.core.annotation.Nullable;
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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/referral-type")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "referral type")
public class ReferralTypeController {

    private static final Logger LOG = LoggerFactory.getLogger(ReferralTypeController.class);

    private final ReferralTypeServices referralTypeServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public ReferralTypeController(ReferralTypeServices referralTypeServices,
                                                   EventLoopGroup eventLoopGroup,
                                                   @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.referralTypeServices = referralTypeServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = (Scheduler) Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Find referral type based on onboardee profile id.
     *
     * @param id {@link UUID} ID of the onboardee profile's referral type
     * @return {@link ReferralTypeDTO } Returned onboardee profile's referral type
     */
    @Get("/{id}")
    public Mono<HttpResponse<ReferralTypeDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> referralTypeServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<ReferralTypeDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Find by employment desired and availability or find all.
     *
     * @param id                   {@link UUID} ID of the onboardee profile
     * @param discoveredOpportunity      {@link String} How applicant discovered this opportunity
     * @param referredBy    {@link String} Whoever referred the applicant
     * @param referrerEmail    {@link String} Email of whoever referred the applicant
     * @param referrerJobSite    {@link String} The job site of the application
     * @param referralTypeOther    {@link String} Other section of the form for additional information
     * @return {@link List <  ReferralTypeDTO  >} List of work preference criteria that match the input parameters
     */

    @Get("/{?discoveredOpportunity,referredBy,referrerEmail,referrerJobSite,referralTypeOther}")
    public Mono<HttpResponse<List<ReferralTypeDTO>>> findByValue(@Nullable UUID id,
                                                                 @Nullable String discoveredOpportunity,
                                                                 @Nullable String referredBy,
                                                                 @Nullable String referrerEmail,
                                                                 @Nullable String referrerJobSite,
                                                                 @Nullable String referralTypeOther) {
        return Mono.fromCallable(() -> referralTypeServices.findByValues(id, discoveredOpportunity, referredBy, referrerEmail, referrerJobSite, referralTypeOther))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(referralTypes -> {
                    List<ReferralTypeDTO> dtoList = referralTypes.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<ReferralTypeDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(scheduler);
    }

    /**
     * Save a new onboardee profile's referral type.
     *
     * @param referralType {@link ReferralTypeDTO } Information of the onboardee's referral type, if any
     * @return {@link ReferralTypeDTO} The created onboardee's referral type
     */
    @Post()
    public Mono<HttpResponse<ReferralTypeDTO>> save(@Body @Valid ReferralTypeDTO referralType) {

        return Mono.fromCallable(() -> referralTypeServices.saveReferralType(fromDTO(referralType)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedReferralType -> (HttpResponse<ReferralTypeDTO>) HttpResponse
                        .created(fromEntity(savedReferralType))
                        .headers(headers -> headers.location(location(savedReferralType.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Update an onboardee profile's referral type.
     *
     * @param referralType {@link ReferralTypeDTO} Information of the onboardee profile's referral type
     *
     * @return {@link ReferralTypeDTO} The updated onboardee's referral type
     */
    @Put()
    public Mono<HttpResponse<ReferralTypeDTO>> update(@Body @Valid ReferralTypeDTO referralType) {
        LOG.info(":)");
        return Mono.fromCallable(() -> referralTypeServices.saveReferralType(fromDTO(referralType)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedReferralType -> {
                    ReferralTypeDTO updatedReferralType = fromEntity(savedReferralType);
                    return (HttpResponse<ReferralTypeDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedReferralType.getId())))
                            .body(updatedReferralType);
                })
                .subscribeOn(scheduler);
    }

    /**
     * Delete an onboardee profile's referral type
     *
     * @param id {@link UUID} Member unique id
     * @return {@link HttpResponse}
     */
    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> referralTypeServices.deleteReferralType(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) { return URI.create("/referral-type/" + id); }

    private ReferralTypeDTO fromEntity(ReferralType entity) {
        ReferralTypeDTO dto = new ReferralTypeDTO();

        dto.setDiscoveredOpportunity(entity.getDiscoveredOpportunity());
        dto.setReferredBy(entity.getReferredBy());
        dto.setReferrerEmail(entity.getReferrerEmail());
        dto.setReferrerJobSite(entity.getReferrerJobSite());
        dto.setReferralTypeOther(entity.getReferralTypeOther());

        return dto;
    }

    private ReferralType fromDTO(ReferralTypeDTO dto) {
        return new ReferralType(dto.getId(), dto.getDiscoveredOpportunity(), dto.getReferredBy(), dto.getReferrerEmail(),
                dto.getReferrerJobSite(), dto.getReferralTypeOther());
    }

}
