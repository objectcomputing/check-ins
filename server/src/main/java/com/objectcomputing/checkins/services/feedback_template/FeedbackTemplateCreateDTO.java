package com.objectcomputing.checkins.services.feedback_template;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Introspected
public class FeedbackTemplateCreateDTO {

    @NotBlank
    @Schema(description = "title of the feedback template")
    private String title;

    @Nullable
    @Schema(description = "description of the feedback template")
    private String description;

    @NotNull
    @Schema(description = "UUID of person who created the feedback template")
    private UUID creatorId;

    @NotNull
    @Schema(description = "whether or not the template is allowed to be used for a feedback request")
    private Boolean active;

    @NotNull
    @Schema(description = "whether the template is accessible to everyone or just the creator")
    private Boolean isPublic;

    @NotNull
    @Schema(description = "whether the template is an ad-hoc template")
    private Boolean isAdHoc;

    @NotNull
    @Schema(description = "whether the template is used for performance reviews")
    private Boolean isReview;

    @Nullable
    @Schema(description = "whether the template is used for external recipients")
    private Boolean isForExternalRecipient;

}
