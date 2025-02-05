package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.security.ImpersonationController;
import com.objectcomputing.checkins.services.feedback_answer.*;
import com.objectcomputing.checkins.services.feedback_answer.question_and_answer.QuestionAndAnswerServices;
import com.objectcomputing.checkins.services.feedback_external_recipient.FeedbackExternalRecipientServices;
import com.objectcomputing.checkins.services.memberprofile.*;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.http.netty.cookies.NettyCookie;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Validated
@Controller("/services/feedback/requests/external/recipients")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
@Tag(name = "feedback external recipient")
public class FeedbackRequestExternalRecipientController {

    private final FeedbackRequestServices feedbackReqServices;
    private final FeedbackExternalRecipientServices feedbackExternalRecipientServices;
    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestExternalRecipientController.class);
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private final MemberProfileServices memberProfileServices;
    private final QuestionAndAnswerServices questionAndAnswerServices;
    private final FeedbackAnswerServices feedbackAnswerServices;

    public FeedbackRequestExternalRecipientController(
            FeedbackRequestServices feedbackRequestServices, FeedbackExternalRecipientServices feedbackExternalRecipientServices,
            MemberProfileServices memberProfileServices, QuestionAndAnswerServices questionAndAnswerServices,
            FeedbackAnswerServices feedbackAnswerServices
    ) {
        this.feedbackReqServices = feedbackRequestServices;
        this.feedbackExternalRecipientServices = feedbackExternalRecipientServices;
        this.memberProfileServices = memberProfileServices;
        this.questionAndAnswerServices = questionAndAnswerServices;
        this.feedbackAnswerServices = feedbackAnswerServices;
    }

    /**
     * Retrieve feedback request record by ID
     *
     * @param id {@link UUID} ID of feedback request record to retrieve
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Get("/{id}")
    public HttpResponse<FeedbackRequestResponseDTO> getById(UUID id) {
        FeedbackRequest feedbackRequest = getExternal(id);
        return feedbackRequest == null ? HttpResponse.notFound() : HttpResponse.ok(feedbackRequestFromEntity(feedbackRequest))
                .headers(headers -> headers.location(URI.create("/feedback_request" + feedbackRequest.getId())));
    }

    @Get("/verify/{id}")
    public HttpStatus verifyRequest(UUID id) {
        FeedbackRequest feedbackRequest = getExternal(id);
        return feedbackReqServices.verifyExternal(feedbackRequest)
                   ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_ENTITY;
    }

    @Get("/submitForExternalRecipient/{id}")
    public HttpResponse<?> redirectToReactPage(UUID id) {
        LOG.info("FeedbackRequestExternalRecipientController, redirectToReactPage, id: {}", id);
        return HttpResponse.redirect(URI.create("/feedback/submitForExternalRecipient?id=" + id));
    }

    /**
     * Update a feedback request
     *
     * @param requestBody {@link FeedbackRequestUpdateDTO} The updated feedback request
     * @return {@link FeedbackRequestResponseDTO}
     */
    @Put
    public HttpResponse<FeedbackRequestResponseDTO> update(@Body @Valid @NotNull FeedbackRequestUpdateDTO requestBody) {
        if (requestBody.getExternalRecipientId() == null) {
            throw new BadArgException("Missing required parameter: externalRecipientId");
        }
        FeedbackRequest savedFeedback = feedbackReqServices.update(requestBody);
        return HttpResponse.ok(feedbackRequestFromEntity(savedFeedback))
                .headers(headers -> headers.location(URI.create("/feedback_request/" + savedFeedback.getId())));
    }

    private FeedbackRequest getExternal(UUID id) {
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(id);
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }
        return feedbackRequest;
    }

    private FeedbackRequestResponseDTO feedbackRequestFromEntity(FeedbackRequest feedbackRequest) {
        FeedbackRequestResponseDTO dto = new FeedbackRequestResponseDTO();
        dto.setId(feedbackRequest.getId());
        dto.setCreatorId(feedbackRequest.getCreatorId());
        dto.setRequesteeId(feedbackRequest.getRequesteeId());
        dto.setRecipientId(feedbackRequest.getRecipientId());
        dto.setTemplateId(feedbackRequest.getTemplateId());
        dto.setSendDate(feedbackRequest.getSendDate());
        dto.setDueDate(feedbackRequest.getDueDate());
        dto.setStatus(feedbackRequest.getStatus());
        dto.setSubmitDate(feedbackRequest.getSubmitDate());
        dto.setReviewPeriodId(feedbackRequest.getReviewPeriodId());
        dto.setExternalRecipientId(feedbackRequest.getExternalRecipientId());
        return dto;
    }

    private MemberProfile fromDTO(MemberProfileUpdateDTO dto) {
        return new MemberProfile(dto.getId(), dto.getFirstName(), dto.getMiddleName(), dto.getLastName(),
                dto.getSuffix(), dto.getTitle(), dto.getPdlId(), dto.getLocation(), dto.getWorkEmail(),
                dto.getEmployeeId(), dto.getStartDate(), dto.getBioText(), dto.getSupervisorid(),
                dto.getTerminationDate(), dto.getBirthDay(), dto.getVoluntary(), dto.getExcluded(), dto.getLastSeen());
    }

    private MemberProfile fromDTO(MemberProfileCreateDTO dto) {
        return new MemberProfile(dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getSuffix(),
                dto.getTitle(), dto.getPdlId(), dto.getLocation(), dto.getWorkEmail(), dto.getEmployeeId(),
                dto.getStartDate(), dto.getBioText(), dto.getSupervisorid(), dto.getTerminationDate(), dto.getBirthDay(),
                dto.getVoluntary(), dto.getExcluded(), dto.getLastSeen());
    }

    private MemberProfileResponseDTO fromEntity(MemberProfile entity) {
        MemberProfileResponseDTO dto = new MemberProfileResponseDTO();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setSuffix(entity.getSuffix());
        dto.setName(MemberProfileUtils.getFullName(entity));
        dto.setTitle(entity.getTitle());
        dto.setPdlId(entity.getPdlId());
        dto.setLocation(entity.getLocation());
        dto.setWorkEmail(entity.getWorkEmail());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setStartDate(entity.getStartDate());
        dto.setBioText(entity.getBioText());
        dto.setSupervisorid(entity.getSupervisorid());
        dto.setTerminationDate(entity.getTerminationDate());
        dto.setBirthDay(entity.getBirthDate());
        dto.setLastSeen(entity.getLastSeen());
        return dto;
    }

    protected URI location(UUID id) {
        return URI.create("/member-profiles/" + id);
    }

    @Get("/csrf/cookie")
    public HttpResponse <?> getCsrfToken()  {
        LOG.info("CsrfModelProcessor, getCsrfToken");
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        String cookieValue = base64Encoder.encodeToString(randomBytes);

        return HttpResponse.ok()
                // set cookie
                .cookie(new NettyCookie("_csrf", cookieValue).path("/").sameSite(SameSite.Strict)).body(cookieValue)
                ;
    }

    /**
     * Find requestee's member profile for the given FeedbackRequest ID
     *
     * @param id {@link UUID} ID of the feedback-reqeust record
     * @return {@link MemberProfileResponseDTO} Returned member profile
     */
    @Get("/getRequesteeForFeedbackRequest/{id}")
    public HttpResponse<MemberProfileResponseDTO> getRequesteeForFeedbackRequest(UUID id) {
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(id);
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }

        MemberProfile memberProfile = memberProfileServices.getById(feedbackRequest.getRequesteeId());
        return HttpResponse.ok(fromEntity(memberProfile))
                .headers(headers -> headers.location(location(memberProfile.getId())));
    }

    /**
     * Find requester's member profile for the given FeedbackRequest ID
     *
     * @param id {@link UUID} ID of the feedback-reqeust record
     * @return {@link MemberProfileResponseDTO} Returned member profile
     */
    @Get("/getRequesterForFeedbackRequest/{id}")
    public HttpResponse<MemberProfileResponseDTO> getRequesterForFeedbackRequest(UUID id) {
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(id);
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }

        MemberProfile memberProfile = memberProfileServices.getById(feedbackRequest.getCreatorId());
        return HttpResponse.ok(fromEntity(memberProfile))
                .headers(headers -> headers.location(location(memberProfile.getId())));
    }

    @Get("/getAllQuestionsAndAnswers/{requestId}")
    public List<QuestionAndAnswerServices.Tuple> getAllQuestionsAndAnswers(@Nullable UUID requestId) {
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(requestId);
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }
        return questionAndAnswerServices.getAllQuestionsAndAnswers(requestId);
    }

    /**
     * Create a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerCreateDTO} New feedback answer to create
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Post("/feedback/answers")
    public HttpResponse<FeedbackAnswerResponseDTO> save(@Body @Valid @NotNull FeedbackAnswerCreateDTO requestBody) {
        FeedbackAnswer feedbackAnswer = fromDTO(requestBody);
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(feedbackAnswer.getRequestId());
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }
        FeedbackAnswer savedAnswer = feedbackAnswerServices.save(feedbackAnswer);
        return HttpResponse.created(fromEntity(savedAnswer))
                .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())))
                ;
    }

    /**
     * Update a feedback answer
     *
     * @param requestBody {@link FeedbackAnswerUpdateDTO} The updated feedback answer
     * @return {@link FeedbackAnswerResponseDTO}
     */
    @Put("/feedback/answers")
    public HttpResponse<FeedbackAnswerResponseDTO> update(@Body @Valid @NotNull FeedbackAnswerUpdateDTO requestBody) {
        FeedbackAnswer feedbackAnswer = feedbackAnswerServices.getById(fromDTO(requestBody).getId());
        FeedbackRequest feedbackRequest = feedbackReqServices.getById(feedbackAnswer.getRequestId());
        if (feedbackRequest.getExternalRecipientId() == null) {
            throw new BadArgException("This feedback request is not for an external recipient");
        }
        FeedbackAnswer savedAnswer = feedbackAnswerServices.update(feedbackAnswer);
        return HttpResponse.ok(fromEntity(savedAnswer))
                .headers(headers -> headers.location(URI.create("/feedback_answer/" + savedAnswer.getId())))
                ;
    }

    private FeedbackAnswer fromDTO(FeedbackAnswerCreateDTO dto) {
        return new FeedbackAnswer(dto.getAnswer(), dto.getQuestionId(), dto.getRequestId(), dto.getSentiment());
    }

    private FeedbackAnswer fromDTO(FeedbackAnswerUpdateDTO dto) {
        return new FeedbackAnswer(dto.getId(), dto.getAnswer(), dto.getSentiment());
    }

    private FeedbackAnswerResponseDTO fromEntity(FeedbackAnswer feedbackAnswer) {
        FeedbackAnswerResponseDTO dto = new FeedbackAnswerResponseDTO();
        dto.setId(feedbackAnswer.getId());
        dto.setAnswer(feedbackAnswer.getAnswer());
        dto.setQuestionId(feedbackAnswer.getQuestionId());
        dto.setRequestId(feedbackAnswer.getRequestId());
        dto.setSentiment(feedbackAnswer.getSentiment());
        return dto;
    }

}
