package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.stream.Collectors;

import javax.annotation.Nullable;
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
    private final ExecutorService executorService;

    public FeedbackTemplateController(FeedbackTemplateServices feedbackTemplateServices,
                                      EventLoopGroup eventLoopGroup,
                                      ExecutorService executorService) {
        this.feedbackTemplateServices = feedbackTemplateServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }

    /**
     * Create a feedback template
     *
     * @param requestBody {@link FeedbackTemplateCreateDTO} New feedback template to create
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FeedbackTemplateResponseDTO>> save(@Body @Valid @NotNull FeedbackTemplateCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackTemplateServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .created(fromEntity(savedTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_templates/" + savedTemplate.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Update a feedback template
     *
     * @param requestBody {@link FeedbackTemplateUpdateDTO} The updated feedback template
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Put()
    public Single<HttpResponse<FeedbackTemplateResponseDTO>> update(@Body @Valid @NotNull FeedbackTemplateUpdateDTO requestBody) {
        return Single.fromCallable(() -> feedbackTemplateServices.update(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_template/" + savedTemplate.getId())))
                        .body(fromEntity(savedTemplate)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback template
     *
     * @param id {@link UUID} ID of the feedback template being deleted
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Delete("/{id}")
    public Single<? extends HttpResponse<?>> delete(@NotNull UUID id) {
        return Single.fromCallable(() -> feedbackTemplateServices.delete(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(success -> (HttpResponse<?>) HttpResponse.ok())
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback template by ID
     *
     * @param id {@link UUID} ID of the requested feedback template
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FeedbackTemplateResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> feedbackTemplateServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(template -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse.ok(fromEntity(template)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback templates by title or by the creator id, filter by active status
     *
     * @param title {@link String} Title of feedback template
     * @param creatorId {@link UUID} UUID of creator
     * @return {@link List<FeedbackTemplateResponseDTO>} List of feedback templates that match the input parameters
     */
    @Get("/{?creatorId,title}")
    public Single<HttpResponse<List<FeedbackTemplateResponseDTO>>> findByValues(@Nullable UUID creatorId, @Nullable String title) {
        return Single.fromCallable(() -> feedbackTemplateServices.findByFields(creatorId, title))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackTemplates -> {
                    List<FeedbackTemplateResponseDTO> dtoList = feedbackTemplates.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackTemplateResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Converts a {@link FeedbackTemplateCreateDTO} into a {@link FeedbackTemplate}
     * @param dto {@link FeedbackTemplateCreateDTO}
     * @return {@link FeedbackTemplate}
     */
    private FeedbackTemplate fromDTO(FeedbackTemplateCreateDTO dto) {
        return new FeedbackTemplate(dto.getTitle(), dto.getDescription(), dto.getCreatorId(), dto.getIsAdHoc());
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
        dto.setIsAdHoc(feedbackTemplate.getIsAdHoc());
        return dto;
    }

}
