package com.objectcomputing.checkins.services.frozen_template;

import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Introspected
public class FrozenTemplateCreateDTO {

    @NotBlank
    @Schema(description = "title of feedback template", required = true)
    private String title;

    @Nullable
    @Schema(description = "description of feedback template")
    private String description;

    @NotBlank
    @Schema(description = "UUID of person who created the original feedback template, not necessarily request creator", required = true)
    private UUID originalCreatorId;

    @NotBlank
    @Schema(description = "UUID of the request this frozen template is attached to ", required = true)
    private UUID requestId;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }


    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public UUID getOriginalCreatorId() {
        return originalCreatorId;
    }

    public void setOriginalCreatorId(UUID originalCreatorId) {
        this.originalCreatorId = originalCreatorId;
    }




}
