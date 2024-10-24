package com.objectcomputing.checkins.services.feedback.suggestions;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Controller("/services/feedback/suggestions")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "feedback")
public class FeedbackSuggestionsController {

    private final FeedbackSuggestionsService suggestionsService;

    public FeedbackSuggestionsController(FeedbackSuggestionsService suggestionsService) {
        this.suggestionsService = suggestionsService;
    }

    @Get("/{id}")
    public List<FeedbackSuggestionDTO> getSuggestionsByProfileId(UUID id) {
        return suggestionsService.getSuggestionsByProfileId(id);
    }
}
