package com.objectcomputing.checkins.services.feedback_template;

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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/templates")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "feedback_templates")
public class FeedbackTemplateController {
    private final FeedbackTemplateServices feedbackTemplateServices;
    private final EventLoopGroup eventLoopGroup;
    private final Scheduler scheduler;

    public FeedbackTemplateController(FeedbackTemplateServices feedbackTemplateServices,
                                      EventLoopGroup eventLoopGroup,
                                      @Named(TaskExecutors.IO) ExecutorService executorService) {
        this.feedbackTemplateServices = feedbackTemplateServices;
        this.eventLoopGroup = eventLoopGroup;
        this.scheduler = Schedulers.fromExecutorService(executorService);
    }

    /**
     * Create a feedback template
     *
     * @param requestBody {@link FeedbackTemplateCreateDTO} New feedback template to create
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Post()
    public Mono<HttpResponse<FeedbackTemplateResponseDTO>> save(@Body @Valid @NotNull FeedbackTemplateCreateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackTemplateServices.save(fromDTO(requestBody)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .created(fromEntity(savedTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_templates/" + savedTemplate.getId()))))
                .subscribeOn(scheduler);
    }

    /**
     * Update a feedback template
     *
     * @param requestBody {@link FeedbackTemplateUpdateDTO} The updated feedback template
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Put()
    public Mono<HttpResponse<FeedbackTemplateResponseDTO>> update(@Body @Valid @NotNull FeedbackTemplateUpdateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackTemplateServices.update(fromDTO(requestBody)))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(savedTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_template/" + savedTemplate.getId())))
                        .body(fromEntity(savedTemplate)))
                .subscribeOn(scheduler);
    }

    /**
     * Delete a feedback template
     *
     * @param id {@link UUID} ID of the feedback template being deleted
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Delete("/{id}")
    public Mono<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromCallable(() -> feedbackTemplateServices.delete(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    /**
     * Delete all ad-hoc feedback templates that are created by a specific member
     * @param creatorId The {@link UUID} of the creator of the ad-hoc template(s)
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Delete("/creator/{creatorId}")
    public Mono<? extends HttpResponse<?>> deleteByCreatorId(@Nullable UUID creatorId) {
        return Mono.fromCallable(() -> feedbackTemplateServices.setAdHocInactiveByCreator(creatorId))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(scheduler);
    }

    /**
     * Get feedback template by ID
     *
     * @param id {@link UUID} ID of the requested feedback template
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Get("/{id}")
    public Mono<HttpResponse<FeedbackTemplateResponseDTO>> getById(UUID id) {
        return Mono.fromCallable(() -> feedbackTemplateServices.getById(id))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(template -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse.ok(fromEntity(template)))
                .subscribeOn(scheduler);
    }

    /**
     * Get feedback templates by title or by the creator id, filter by active status
     *
     * @param title {@link String} Title of feedback template
     * @param creatorId {@link UUID} UUID of creator
     * @return {@link List<FeedbackTemplateResponseDTO>} List of feedback templates that match the input parameters
     */
    @Get("/{?creatorId,title}")
    public Mono<HttpResponse<List<FeedbackTemplateResponseDTO>>> findByValues(@Nullable UUID creatorId, @Nullable String title) {
        return Mono.fromCallable(() -> feedbackTemplateServices.findByFields(creatorId, title))
                .publishOn(Schedulers.fromExecutor(eventLoopGroup))
                .map(feedbackTemplates -> {
                    List<FeedbackTemplateResponseDTO> dtoList = feedbackTemplates.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackTemplateResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(scheduler);
    }

    /**
     * Converts a {@link FeedbackTemplateCreateDTO} into a {@link FeedbackTemplate}
     * @param dto {@link FeedbackTemplateCreateDTO}
     * @return {@link FeedbackTemplate}
     */
    private FeedbackTemplate fromDTO(FeedbackTemplateCreateDTO dto) {
        return new FeedbackTemplate(dto.getTitle(), dto.getDescription(), dto.getCreatorId(), dto.getIsPublic(), dto.getIsAdHoc());
    }

    /**
     * Converts a {@link FeedbackTemplateUpdateDTO} into a {@link FeedbackTemplate}
     * @param dto {@link FeedbackTemplateUpdateDTO}
     * @return {@link FeedbackTemplate}
     */
    private FeedbackTemplate fromDTO(FeedbackTemplateUpdateDTO dto) {
        return new FeedbackTemplate(dto.getId(), dto.getActive());
    }

    /**
     * Converts a {@link FeedbackTemplate} into a {@link FeedbackTemplateResponseDTO}
     * @param feedbackTemplate {@link FeedbackTemplate}
     * @return {@link FeedbackTemplateResponseDTO}
     */
    private FeedbackTemplateResponseDTO fromEntity(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplateResponseDTO dto = new FeedbackTemplateResponseDTO();
        dto.setId(feedbackTemplate.getId());
        dto.setTitle(feedbackTemplate.getTitle());
        dto.setDescription(feedbackTemplate.getDescription());
        dto.setCreatorId(feedbackTemplate.getCreatorId());
        dto.setDateCreated(feedbackTemplate.getDateCreated());
        dto.setActive(feedbackTemplate.getActive());
        dto.setIsPublic(feedbackTemplate.getIsPublic());
        dto.setIsAdHoc(feedbackTemplate.getIsAdHoc());
        return dto;
    }

}
