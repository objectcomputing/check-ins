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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(FeedbackTemplateServices.class);

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
     * @param requestBody {@link FeedbackTemplateCreateDTO} New feedback templat4e to create
     * @return {@link FeedbackTemplateResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FeedbackTemplateResponseDTO>> save(@Body @Valid @NotNull FeedbackTemplateCreateDTO requestBody) {
        return Single.fromCallable(() -> feedbackTemplateServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_templates/" + savedFeedbackTemplate.getId()))))
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
                .map(savedFeedbackTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .ok()
                        .headers(headers -> headers.location(URI.create("/feedback_template/" + savedFeedbackTemplate.getId())))
                        .body(fromEntity(savedFeedbackTemplate)))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Delete a feedback template
     *
     * @param id {@link UUID} ID of the feedback template being deleted
     * @return {@link FeedbackTemplateResponseDTO}
     */
//    @Delete("/{id}")
//    public Single<HttpResponse> delete(@NotNull UUID id) {
//        return Single.fromCallable(() -> feedbackTemplateServices.delete(id))
//                .observeOn(Schedulers.from(eventLoopGroup))
//                .map(successFlag -> (HttpResponse) HttpResponse.ok())
//                .subscribeOn(Schedulers.from(executorService));
//    }

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
                .map(feedbackTemplate -> (HttpResponse<FeedbackTemplateResponseDTO>) HttpResponse
                        .ok(fromEntity(feedbackTemplate))
                        .headers(headers -> headers.location(URI.create("/feedback_template/" + feedbackTemplate.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get feedback templates by title or by the creator id
     *
     * @param title {@link String} Title of feedback template
     * @param createdBy {@link UUID} UUID of creator
     * @return {@link List<FeedbackTemplateResponseDTO>} List of feedback templates that match the input parameters
     */
    @Get("/?createdBy,title")
    public Single<HttpResponse<List<FeedbackTemplateResponseDTO>>> findByValues(@Nullable UUID createdBy, @Nullable String title) {
        LOG.info("In controller: {}, {}", createdBy, title);
        return Single.fromCallable(() -> feedbackTemplateServices.findByFields(createdBy, title))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackTemplates -> {
                    List<FeedbackTemplateResponseDTO> dtoList = feedbackTemplates.stream()
                            .map(this::fromEntity).collect(Collectors.toList());
                    return (HttpResponse<List<FeedbackTemplateResponseDTO>>) HttpResponse.ok(dtoList);
                }).subscribeOn(Schedulers.from(executorService));
    }

    private FeedbackTemplateResponseDTO fromEntity(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplateResponseDTO dto = new FeedbackTemplateResponseDTO();
        dto.setId(feedbackTemplate.getId());
        dto.setTitle(feedbackTemplate.getTitle());
        dto.setDescription(feedbackTemplate.getDescription());
        dto.setCreatedBy(feedbackTemplate.getCreatedBy());
        return dto;
    }

    private FeedbackTemplate fromDTO(FeedbackTemplateCreateDTO dto) {
        return new FeedbackTemplate(dto.getTitle(), dto.getDescription(), dto.getCreatedBy());
    }

    private FeedbackTemplate fromDTO(FeedbackTemplateUpdateDTO dto) {
        return new FeedbackTemplate(dto.getId(), dto.getTitle(), dto.getDescription());
    }

}
