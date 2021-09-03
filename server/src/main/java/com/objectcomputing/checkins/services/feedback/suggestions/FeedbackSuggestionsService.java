package com.objectcomputing.checkins.services.feedback.suggestions;

import java.util.UUID;
import java.util.List;

public interface FeedbackSuggestionsService {
    List<FeedbackSuggestionDTO> getSuggestionsByProfileId(UUID id);
}
