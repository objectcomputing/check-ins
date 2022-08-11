package com.objectcomputing.checkins.services.employmentpreferences;

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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Controller("/services/employment-desired-availability")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "employment desired availability")
public class EmploymentDesiredAvailabilityController {

    private static final Logger LOG = LoggerFactory.getLogger(EmploymentDesiredAvailabilityController.class);

    private final EmploymentDesiredAvailabilityServices employmentDesiredAvailabilityServices;

    private final EventLoopGroup eventLoopGroup;

    private final Scheduler scheduler;

    public EmploymentDesiredAvailabilityController(EmploymentDesiredAvailabilityServices employmentDesiredAvailabilityServices,
                                    EventLoopGroup eventLoopGroup,
                                    @Named(TaskExecutors.IO) ExecutorService ioExecutorService) {
        this.employmentDesiredAvailabilityServices = employmentDesiredAvailabilityServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = (Scheduler) Schedulers.fromExecutorService(ioExecutorService);
    }

    /**
     * Find employment desired and availability based on onboardee profile id.
     *
     * @param id {@link UUID} ID of the onboardee profile's employment desired and availability
     * @return {@link EmploymentDesiredAvailabilityDTO } Returned onboardee profile's employment desired and availability
     */
    @Get("/{id}")
    public Mono<HttpResponse<EmploymentDesiredAvailabilityDTO>> getById(UUID id) {

        return Mono.fromCallable(() -> employmentDesiredAvailabilityServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(profile -> (HttpResponse<EmploymentDesiredAvailabilityDTO>) HttpResponse
                        .ok(fromEntity(profile))
                        .headers(headers -> headers.location(location(profile.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Find by employment desired and availability or find all.
     *
     * @param id                   {@link UUID} ID of the onboardee profile
     * @param desiredPosition      {@link String} Find applicant's desired position
     * @param desiredStartDate     {@link LocalDate} Find applicant's desired start date
     * @param currentlyEmployed    {@link Boolean} Find applicant's current employment status
     * @return {@link List <  EmploymentDesiredAvailabilityDTO  >} List of work preference criteria that match the input parameters
     */

    @Get("/{?desiredPosition,desiredStartDate,currentlyEmployed,referredBy,referrerEmail}")
    public Mono<HttpResponse<List<EmploymentDesiredAvailabilityDTO>>> findByValue(@Nullable UUID id,
                                                                                  @Nullable String desiredPosition,
                                                                                  @Nullable LocalDate desiredStartDate,
                                                                                  @Nullable String desiredSalary,
                                                                                  @Nullable Boolean currentlyEmployed,
                                                                                  @Nullable Boolean contactCurrentEmployer,
                                                                                  @Nullable Boolean previousEmploymentOCI,
                                                                                  @Nullable Boolean noncompeteAgreement,
                                                                                  @Nullable LocalDate noncompeteExpirationDate) {
        return Mono.fromCallable(() -> employmentDesiredAvailabilityServices.findByValues(id, desiredPosition, desiredStartDate, desiredSalary, currentlyEmployed, contactCurrentEmployer, previousEmploymentOCI, noncompeteAgreement, noncompeteExpirationDate))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(workPreference -> {
                    List<EmploymentDesiredAvailabilityDTO> dtoList = workPreference.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<EmploymentDesiredAvailabilityDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(scheduler);
    }

    /**
     * Save a new onboardee profile's employment desired and availability.
     *
     * @param employmentDesiredAvailability {@link EmploymentDesiredAvailabilityCreateDTO } Information of the onboardee's desired employment and availability
     * @return {@link EmploymentDesiredAvailabilityDTO} The created onboardee's desired employment and availability
     */
    @Post()
    public Mono<HttpResponse<EmploymentDesiredAvailabilityDTO>> save(@Body @Valid EmploymentDesiredAvailabilityCreateDTO employmentDesiredAvailability) {

        return Mono.fromCallable(() -> employmentDesiredAvailabilityServices.savePreferences(fromDTO(employmentDesiredAvailability)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedPreference -> (HttpResponse<EmploymentDesiredAvailabilityDTO>) HttpResponse
                        .created(fromEntity(savedPreference))
                        .headers(headers -> headers.location(location(savedPreference.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Update an onboardee profile's desired employment and availability.
     *
     * @param employmentDesiredAvailability {@link EmploymentDesiredAvailabilityDTO} Information of the onboardee profile's desired employment and availability being updated
     *
     * @return {@link EmploymentDesiredAvailabilityDTO} The updated onboardee's desired employment and availability
     */
    @Put()
    public Mono<HttpResponse<EmploymentDesiredAvailabilityDTO>> update(@Body @Valid EmploymentDesiredAvailabilityDTO employmentDesiredAvailability) {
        LOG.info(":)");
        return Mono.fromCallable(() -> employmentDesiredAvailabilityServices.savePreferences(fromDTO(employmentDesiredAvailability)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedPreference -> {
                    EmploymentDesiredAvailabilityDTO updatedWorkPreference = fromEntity(savedPreference);
                    return (HttpResponse<EmploymentDesiredAvailabilityDTO>) HttpResponse
                            .ok()
                            .headers(headers -> headers.location(location(updatedWorkPreference.getId())))
                            .body(updatedWorkPreference);
                })
                .subscribeOn(scheduler);
    }

    /**
     * Delete a onboardee profile's desired employment and availability
     *
     * @param id {@link UUID} Member unique id
     * @return {@link HttpResponse}
     */
    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> employmentDesiredAvailabilityServices.deletePreferences(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(successFlag -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    protected URI location(UUID id) { return URI.create("/employment-desired-availability/" + id); }

    private EmploymentDesiredAvailabilityDTO fromEntity(EmploymentDesiredAvailability entity) {
        EmploymentDesiredAvailabilityDTO dto = new EmploymentDesiredAvailabilityDTO();

        dto.setId(entity.getId());
        dto.setDesiredPosition(entity.getDesiredPosition());
        dto.setDesiredStartDate(entity.getDesiredStartDate());
        dto.setDesiredSalary(entity.getDesiredSalary());
        dto.setCurrentlyEmployed(entity.getCurrentlyEmployed());
        dto.setContactCurrentEmployer(entity.getContactCurrentEmployer());
        dto.setPreviousEmploymentOCI(entity.getPreviousEmploymentOCI());
        dto.setNoncompeteAgreement(entity.getNoncompeteAgreement());
        dto.setNoncompeteExpirationDate(entity.getNoncompeteExpirationDate());

        return dto;
    }

    private EmploymentDesiredAvailability fromDTO(EmploymentDesiredAvailabilityDTO dto) {
        return new EmploymentDesiredAvailability(dto.getId(), dto.getDesiredPosition(), dto.getDesiredStartDate(), dto.getDesiredSalary(),
                dto.getCurrentlyEmployed(), dto.getContactCurrentEmployer(), dto.getPreviousEmploymentOCI(), dto.getNoncompeteAgreement(),
                dto.getNoncompeteExpirationDate());
    }

    private EmploymentDesiredAvailability fromDTO(EmploymentDesiredAvailabilityCreateDTO dto) {
        return new EmploymentDesiredAvailability(dto.getDesiredPosition(), dto.getDesiredStartDate(), dto.getDesiredSalary(),
                dto.getCurrentlyEmployed(), dto.getContactCurrentEmployer(), dto.getPreviousEmploymentOCI(), dto.getNoncompeteAgreement(),
                dto.getNoncompeteExpirationDate());
    }

}
