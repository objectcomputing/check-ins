package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/services/feedback/templates")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "feedback_templates")
public class FeedbackTemplateController {
    private final FeedbackTemplateServices feedbackTemplateServices;

    public FeedbackTemplateController(FeedbackTemplateServices feedbackTemplateServices) {
        this.feedbackTemplateServices = feedbackTemplateServices;
    }

    /**
     * Create a feedback template
     *
     * @param requestBody {@link FeedbackTemplateCreateDTO} New feedback template to create
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Post
    public Mono<HttpResponse<FeedbackTemplateResponseDTO>> save(@Body @Valid @NotNull FeedbackTemplateCreateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackTemplateServices.save(fromDTO(requestBody)))
                .map(savedTemplate -> HttpResponse.created(fromEntity(savedTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_templates/" + savedTemplate.getId()))));
    }

    /**
     * Update a feedback template
     *
     * @param requestBody {@link FeedbackTemplateUpdateDTO} The updated feedback template
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Put
    public Mono<HttpResponse<FeedbackTemplateResponseDTO>> update(@Body @Valid @NotNull FeedbackTemplateUpdateDTO requestBody) {
        return Mono.fromCallable(() -> feedbackTemplateServices.update(fromDTO(requestBody)))
                .map(savedTemplate -> HttpResponse.ok(fromEntity(savedTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_template/" + savedTemplate.getId()))));
    }

    /**
     * Delete a feedback template
     *
     * @param id {@link UUID} ID of the feedback template being deleted
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Delete("/{id}")
    public Mono<HttpResponse<?>> delete(@NotNull UUID id) {
        return Mono.fromRunnable(() -> feedbackTemplateServices.delete(id))
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Delete all ad-hoc feedback templates that are created by a specific member
     * @param creatorId The {@link UUID} of the creator of the ad-hoc template(s)
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Delete("/creator/{creatorId}")
    public Mono<? extends HttpResponse<?>> deleteByCreatorId(@Nullable UUID creatorId) {
        return Mono.fromCallable(() -> feedbackTemplateServices.setAdHocInactiveByCreator(creatorId))
                .map(success -> (HttpResponse<?>) HttpResponse.ok());
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
                .map(template -> HttpResponse.ok(fromEntity(template)));
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
                .map(entities -> entities.stream().map(this::fromEntity).collect(Collectors.toList()))
                .map(HttpResponse::ok);
    }

    /**
     * Converts a {@link FeedbackTemplateCreateDTO} into a {@link FeedbackTemplate}
     * @param dto {@link FeedbackTemplateCreateDTO}
     * @return {@link FeedbackTemplate}
     */
    private FeedbackTemplate fromDTO(FeedbackTemplateCreateDTO dto) {
        return new FeedbackTemplate(dto.getTitle(), dto.getDescription(), dto.getCreatorId(), dto.getIsPublic(), dto.getIsAdHoc(), dto.getIsReview());
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
