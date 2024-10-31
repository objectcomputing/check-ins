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
    @RequiredPermission(Permission.CAN_CREATE_FEEDBACK_REQUEST)
    @Post
    public HttpResponse<FeedbackExternalRecipientResponseDTO> save(@Body @Valid @NotNull FeedbackExternalRecipientCreateDTO feedbackExternalRecipientCreateDTO) {
        FeedbackExternalRecipient savedFeedbackExternalRecipient;
        FeedbackExternalRecipient feedbackExternalRecipientFromDto = fromDTO(feedbackExternalRecipientCreateDTO);
        try {
            savedFeedbackExternalRecipient = feedbackExternalRecipientServices.save(feedbackExternalRecipientFromDto);
        } catch (Exception e) {
            throw e;
        }
        return HttpResponse.created(fromEntity(savedFeedbackExternalRecipient))
                .headers(headers -> headers.location(URI.create("/feedback_external_recipient/" + savedFeedbackExternalRecipient.getId())));
    }

    /*
    @Get("/{?email, firstName, lastName, companyName}")
    public List<FeedbackExternalRecipientResponseDTO> findByValues(@Nullable String email, @Nullable String firstName, @Nullable String lastName, @Nullable String companyName) {
        return this.feedbackExternalRecipientServices.findByValues(email, firstName, lastName, companyName)
                .stream()
                .map(this::fromEntity)
                .toList();
    }
    */

    /**
     * Return list of all external-recipients used for feedback-requests
     *
     * @return list of {@link FeedbackExternalRecipientResponseDTO}
     */
    @Get("/findAll")
    public List<FeedbackExternalRecipientResponseDTO> findAll() {
        return this.feedbackExternalRecipientServices.findAll()
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
        return object;
    }

    private FeedbackExternalRecipientResponseDTO fromEntity(FeedbackExternalRecipient feedbackExternalRecipient) {
        FeedbackExternalRecipientResponseDTO dto = new FeedbackExternalRecipientResponseDTO();
        dto.setId(feedbackExternalRecipient.getId());
        dto.setEmail(feedbackExternalRecipient.getEmail());
        dto.setFirstName(feedbackExternalRecipient.getFirstName());
        dto.setLastName(feedbackExternalRecipient.getLastName());
        dto.setCompanyName(feedbackExternalRecipient.getCompanyName());
        return dto;
    }

}
