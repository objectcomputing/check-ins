package com.objectcomputing.checkins.services.feedback_external_recipient;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.feedback_request.*;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Validated
@Controller("/services/feedback/external/recipients")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_AUTHENTICATED)
@Tag(name = "feedback external recipient")
public class FeedbackExternalRecipientController {

    private final FeedbackExternalRecipientServices feedbackExternalRecipientServices;

    public FeedbackExternalRecipientController(FeedbackExternalRecipientServices feedbackExternalRecipientServices) {
        this.feedbackExternalRecipientServices = feedbackExternalRecipientServices;
    }

    /**
     * Create a feedback request external recipient
     *
     * @param feedbackExternalRecipientCreateDTO {@link FeedbackExternalRecipientCreateDTO} New feedback-req external recipient to create
     * @return {@link FeedbackExternalRecipient}
     */
    @Post
    public HttpResponse<FeedbackExternalRecipientResponseDTO> save(@Body @Valid @NotNull FeedbackExternalRecipientCreateDTO feedbackExternalRecipientCreateDTO) {
        FeedbackExternalRecipient savedFeedbackExternalRecipient;
        FeedbackExternalRecipient feedbackExternalRecipientFromDto = fromDTO(feedbackExternalRecipientCreateDTO);
        savedFeedbackExternalRecipient = feedbackExternalRecipientServices.save(feedbackExternalRecipientFromDto);
        return HttpResponse.created(fromEntity(savedFeedbackExternalRecipient))
                .headers(headers -> headers.location(URI.create("/feedback_external_recipient/" + savedFeedbackExternalRecipient.getId())));
    }

    /**
     * Update a feedback request external recipient
     *
     * @param email   The {@link String} email of the external-recipient
     * @param firstName The {@link String} first name of the external-recipient
     * @param lastName The {@link String} last name of the external-recipient
     * @param companyName The {@link String} company name of the external-recipient
     * @param inactive The {@link Boolean} inactive status of the external-recipient
     * @return {@link FeedbackExternalRecipient}
     */
    @Get("/{?email, firstName, lastName, companyName,inactive}")
    public List<FeedbackExternalRecipientResponseDTO> findByValues(@Nullable String email, @Nullable String firstName, @Nullable String lastName, @Nullable String companyName, @Nullable Boolean inactive) {
        return this.feedbackExternalRecipientServices.findByValues(email, firstName, lastName, companyName, inactive)
                .stream()
                .map(this::fromEntity)
                .toList();
    }

    /**
     * Get external recipient (for feedback requests) by ID
     *
     * @param id {@link UUID} ID of the external-recipient
     * @return {@link FeedbackExternalRecipientResponseDTO}
     */
    @Get("/{id}")
    public HttpResponse<FeedbackExternalRecipientResponseDTO> getById(UUID id) {
        FeedbackExternalRecipient feedbackExternalRecipient = this.feedbackExternalRecipientServices.getById(id);
        return HttpResponse.created(fromEntity(feedbackExternalRecipient));
    }

    private FeedbackExternalRecipient fromDTO(FeedbackExternalRecipientCreateDTO dto) {
        FeedbackExternalRecipient object = new FeedbackExternalRecipient();
        object.setEmail(dto.getEmail());
        object.setFirstName(dto.getFirstName());
        object.setLastName(dto.getLastName());
        object.setCompanyName(dto.getCompanyName());
        object.setInactive(dto.getInactive());
        return object;
    }

    private FeedbackExternalRecipientResponseDTO fromEntity(FeedbackExternalRecipient feedbackExternalRecipient) {
        FeedbackExternalRecipientResponseDTO dto = new FeedbackExternalRecipientResponseDTO();
        dto.setId(feedbackExternalRecipient.getId());
        dto.setEmail(feedbackExternalRecipient.getEmail());
        dto.setFirstName(feedbackExternalRecipient.getFirstName());
        dto.setLastName(feedbackExternalRecipient.getLastName());
        dto.setCompanyName(feedbackExternalRecipient.getCompanyName());
        dto.setInactive(feedbackExternalRecipient.getInactive());
        return dto;
    }

}
