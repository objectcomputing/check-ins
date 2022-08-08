package com.objectcomputing.checkins.services.WorkPreference;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Named;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/work-preference")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "work preference")
public class WorkPreferenceController {

    private static final Logger LOG = LoggerFactory.getLogger(WorkPreferenceController.class);

    private final WorkPreferenceServices workPreferenceServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public WorkPreferenceController(WorkPreferenceServices workPreferenceServices,
                                    EventLoopGroup eventLoopGroup,
                                    @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.workPreferenceServices = workPreferenceServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = (Scheduler) Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Find work preferences based on onboardee profile id.
     *
     * @param id {@link UUID} ID of the onboardee profile's work preference
     * @return {@link WorkPreferenceResponseDTO } Returned onboardee profile's work preferences
     */
    @Get("/{id}")
    public Mono<HttpResponse<WorkPreferenceResponseDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> workPreferenceServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<WorkPreferenceResponseDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Find work preference by or find all.
     *
     * @param id                   {@link UUID} ID of the onboardee profile
     * @param desiredPosition      {@link String} ID of the onboardee
     * @param desiredStartDate     {@link java.time.LocalDate} Find applicant's desired start date
     * @param currentlyEmployed    {@link Boolean} Find applicant's current employment status
     * @param referredBy           {@link String} Find who referred the applicant
     * @param referrerEmail        {@link String} Find the email of the referrer
     * @return {@link List <  WorkPreferenceResponseDTO  >} List of work preference criteria that match the input parameters
     */

    @Get("/{?desiredPosition,desiredStartDate,currentlyEmployed,referredBy,referrerEmail}")
    public Mono<HttpResponse<List<WorkPreferenceResponseDTO>>> findByValue(@Nullable UUID id,
                                                                           @Nullable String desiredPosition,
                                                                           @Nullable LocalDate desiredStartDate,
                                                                           @Nullable Boolean currentlyEmployed,
                                                                           @Nullable String referredBy,
                                                                           @Nullable String referrerEmail) {
        return Mono.fromCallable(() -> workPreferenceServices.findByValues(desiredPosition, desiredStartDate, currentlyEmployed, referredBy, referrerEmail))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(workPreference -> {
                    List<WorkPreferenceResponseDTO> dtoList = workPreference.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<WorkPreferenceResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(scheduler);
    }

    /**
     * Save a new onboardee profile's work preferences.
     *
     * @param workPreference {@link WorkPreferenceCreateDTO } Information of the onboardee's work preferences
     * @return {@link WorkPreferenceResponseDTO} The created onboardee's work preferences
     */
    @Post()
    public Mono<HttpResponse<WorkPreferenceResponseDTO>> save(@Body @Valid WorkPreferenceCreateDTO workPreference) {

        return Mono.fromCallable(() -> workPreferenceServices.savePreferences(fromDTO(workPreference)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedPreference -> (HttpResponse<WorkPreferenceResponseDTO>) HttpResponse
                        .created(fromEntity(savedPreference))
                        .headers(headers -> headers.location(location(savedPreference.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Update an onboardee profile's work preferences.
     *
     * @param workPreference {@link WorkPreferenceResponseDTO} Information of the onboardee profile's work preferences being updated
     *                    *Note: There is no OnboardingProfileUpdateDTO since the information returned in an update is
     *                           the same as the information returned in a response
     *
     * @return {@link WorkPreferenceResponseDTO} The updated onboardee's work preference profile
     */
    @Put()
    public Mono<HttpResponse<WorkPreferenceResponseDTO>> update(@Body @Valid WorkPreferenceResponseDTO workPreference) {
        LOG.info(":)");
        return Mono.fromCallable(() -> workPreferenceServices.savePreferences(fromDTO(workPreference)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedPreference -> {
                    WorkPreferenceResponseDTO updatedWorkPreference = fromEntity(savedPreference);
                    return (HttpResponse<WorkPreferenceResponseDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedWorkPreference.getId())))
                            .body(updatedWorkPreference);
                })
                .subscribeOn(scheduler);
    }

    /**
     * Delete a onboardee profile's work preferences
     *
     * @param id {@link UUID} Member unique id
     * @return {@link HttpResponse}
     */
    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> workPreferenceServices.deletePreferences(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) { return URI.create("/work-preference/" + id); }

    private WorkPreferenceResponseDTO fromEntity(WorkPreference entity) {
        WorkPreferenceResponseDTO dto = new WorkPreferenceResponseDTO();

        dto.setId(entity.getId());
        dto.setDesiredPosition(entity.getDesiredPosition());
        dto.setDesiredStartDate(entity.getDesiredStartDate());
        dto.setDesiredSalary(entity.getDesiredSalary());
        dto.setCurrentlyEmployed(entity.getCurrentlyEmployed());
        dto.setContactCurrentEmployer(entity.getContactCurrentEmployer());
        dto.setPreviousEmploymentOCI(entity.getPreviousEmploymentOCI());
        dto.setNoncompeteAgreement(entity.getNoncompeteAgreement());
        dto.setNoncompeteExpirationDate(entity.getNoncompeteExpirationDate());
        dto.setDiscoveredOpportunity(entity.getDiscoveredOpportunity());
        dto.setReferredBy(entity.getReferredBy());
        dto.setReferrerEmail(entity.getReferrerEmail());
        dto.setReferrerJobSite(entity.getReferrerJobSite());
        dto.setReferralTypeOther(entity.getReferralTypeOther());
        return dto;
    }

    private WorkPreference fromDTO(WorkPreferenceResponseDTO dto) {
        return new WorkPreference(dto.getId(), dto.getDesiredPosition(), dto.getDesiredStartDate(), dto.getDesiredSalary(),
                dto.getCurrentlyEmployed(), dto.getContactCurrentEmployer(), dto.getPreviousEmploymentOCI(), dto.getNoncompeteAgreement(),
                dto.getNoncompeteExpirationDate(), dto.getDiscoveredOpportunity(), dto.getReferredBy(), dto.getReferrerEmail(), dto.getReferrerJobSite(),
                dto.getReferralTypeOther());
    }

    private WorkPreference fromDTO(WorkPreferenceCreateDTO dto) {
        return new WorkPreference(dto.getDesiredPosition(), dto.getDesiredStartDate(), dto.getDesiredSalary(),
                dto.getCurrentlyEmployed(), dto.getContactCurrentEmployer(), dto.getPreviousEmploymentOCI(), dto.getNoncompeteAgreement(),
                dto.getNoncompeteExpirationDate(), dto.getDiscoveredOpportunity(), dto.getReferredBy(), dto.getReferrerEmail(), dto.getReferrerJobSite(),
                dto.getReferralTypeOther());
    }

}
