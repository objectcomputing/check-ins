package com.objectcomputing.checkins.services.frozen_template;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.netty.channel.EventLoopGroup;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Controller("/services/feedback/frozen_templates")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "frozen_templates")
public class FrozenTemplateController {

    private final FrozenTemplateServices frozenTemplateServices;
    private final EventLoopGroup eventLoopGroup;
    private final ExecutorService executorService;

    public FrozenTemplateController (FrozenTemplateServices frozenTemplateServices,
                                     EventLoopGroup eventLoopGroup,
                                     ExecutorService executorService) {
        this.frozenTemplateServices = frozenTemplateServices;
        this.eventLoopGroup = eventLoopGroup;
        this.executorService = executorService;
    }
    /**
     * Create a frozen template that ensures even if original template is deleted,
     * the template info and question attached to a request will never change and never be lost
     *
     * @param requestBody {@link FrozenTemplateCreateDTO} Feedback template info to be frozen
     * @return {@link FrozenTemplateResponseDTO}
     */
    @Post()
    public Single<HttpResponse<FrozenTemplateResponseDTO>> save(@Body @Valid @NotNull FrozenTemplateCreateDTO requestBody) {
        return Single.fromCallable(() -> frozenTemplateServices.save(fromDTO(requestBody)))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(savedFeedbackTemplate -> (HttpResponse<FrozenTemplateResponseDTO>) HttpResponse
                        .created(fromEntity(savedFeedbackTemplate))
                        .headers(headers -> headers.location(URI.create("/frozen_templates/" + savedFeedbackTemplate.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get frozen template by ID
     *
     * @param id {@link UUID} ID of the requested frozen template
     * @return {@link FrozenTemplateResponseDTO}
     */
    @Get("/{id}")
    public Single<HttpResponse<FrozenTemplateResponseDTO>> getById(UUID id) {
        return Single.fromCallable(() -> frozenTemplateServices.getById(id))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackTemplate -> (HttpResponse<FrozenTemplateResponseDTO>) HttpResponse
                        .ok(fromEntity(feedbackTemplate))
                        .headers(headers -> headers.location(URI.create("/frozen_template/" + feedbackTemplate.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    /**
     * Get frozen template attached to certain request
     *
     * @param requestId {@link UUID} ID of the requested frozen template
     * @return {@link FrozenTemplateResponseDTO}
     */

    @Get("/{?requestId}")
    public Single<HttpResponse<FrozenTemplateResponseDTO>> findByValues(@Nullable UUID requestId) {
        return Single.fromCallable(() -> frozenTemplateServices.findByValues(requestId))
                .observeOn(Schedulers.from(eventLoopGroup))
                .map(feedbackTemplate -> (HttpResponse<FrozenTemplateResponseDTO>) HttpResponse
                        .ok(fromEntity(feedbackTemplate))
                        .headers(headers -> headers.location(URI.create("/frozen_template/" + feedbackTemplate.getId()))))
                .subscribeOn(Schedulers.from(executorService));
    }

    private FrozenTemplateResponseDTO fromEntity(FrozenTemplate template) {
        FrozenTemplateResponseDTO dto = new FrozenTemplateResponseDTO();
        dto.setId(template.getId());
        dto.setTitle(template.getTitle());
        dto.setDescription(template.getDescription());
        dto.setCreatedBy(template.getCreatedBy());
        dto.setRequestId(template.getRequestId());
        return dto;
    }

    private FrozenTemplate fromDTO(FrozenTemplateCreateDTO dto) {
       FrozenTemplate template = new FrozenTemplate();
       template.setCreatedBy(dto.getCreatedBy());
       template.setTitle(dto.getTitle());
       template.setDescription(dto.getDescription());
       template.setCreatedBy(dto.getCreatedBy());
       template.setRequestId(dto.getRequestId());
       return template;
    }
}
