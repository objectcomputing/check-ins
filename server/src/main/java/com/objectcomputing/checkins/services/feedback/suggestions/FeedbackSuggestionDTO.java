package com.objectcomputing.checkins.services.feedback.suggestions;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Introspected
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackSuggestionDTO {

    @Nullable
    private String reason;
    @Nullable
    private UUID id;
}
