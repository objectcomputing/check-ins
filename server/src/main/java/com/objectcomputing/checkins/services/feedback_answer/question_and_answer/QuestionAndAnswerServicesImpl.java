package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class QuestionAndAnswerServicesImpl implements QuestionAndAnswerServices {

    private final FeedbackAnswerServices feedbackAnswerServices;
    private final CurrentUserServices currentUserServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final FeedbackRequestServices feedbackRequestServices;
    private final MemberProfileServices memberProfileServices;

    public QuestionAndAnswerServicesImpl(FeedbackAnswerServices feedbackAnswerServices,
                                         CurrentUserServices currentUserServices,
                                         TemplateQuestionServices templateQuestionServices,
                                         FeedbackRequestServices feedbackRequestServices, MemberProfileServices memberProfileServices) {
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.currentUserServices = currentUserServices;
        this.templateQuestionServices = templateQuestionServices;
        this.feedbackRequestServices = feedbackRequestServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public List<Tuple> getAllQuestionsAndAnswers(UUID requestId) {
        FeedbackRequest feedbackRequest = feedbackRequestServices.getById(requestId);
        if (!getIsPermitted(feedbackRequest)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
        List<TemplateQuestion> templateQuestions = templateQuestionServices.findByFields(feedbackRequest.getTemplateId());
        List<FeedbackAnswer> answerList = feedbackAnswerServices.findByValues(null, requestId, null);

        List<Tuple> returnerList = new ArrayList<>();

        if (answerList.isEmpty()) {
            for (TemplateQuestion question: templateQuestions) {
                FeedbackAnswer newAnswerObject = new FeedbackAnswer();
                newAnswerObject.setAnswer(null);
                newAnswerObject.setQuestionId(question.getId());
                newAnswerObject.setRequestId(requestId);
                newAnswerObject.setSentiment(null);
                Tuple newTuple = new Tuple(question, newAnswerObject, feedbackRequest);
                returnerList.add(newTuple);

            }

        } else {
            for (TemplateQuestion question: templateQuestions) {
                FeedbackAnswer foundAnswer = answerList.stream().filter(o -> o.getQuestionId().equals(question.getId())).findFirst().orElse(null);
                Tuple newTuple = new Tuple(question, foundAnswer, feedbackRequest);
                returnerList.add(newTuple);
            }
        }
        return returnerList;
    }

    @Override
    public Tuple getQuestionAndAnswer(@Nullable UUID requestId, @Nullable UUID questionId) {
        TemplateQuestion question = new TemplateQuestion();
        FeedbackRequest feedbackRequest = feedbackRequestServices.getById(requestId);

        if (!getIsPermitted(feedbackRequest)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (questionId != null) {
            question = templateQuestionServices.getById(questionId);
        }

        List<FeedbackAnswer> list;
        list = feedbackAnswerServices.findByValues(questionId, requestId, null);

        FeedbackAnswer returnedAnswer;
        if (list.isEmpty()) {
            FeedbackAnswer newAnswerObject = new FeedbackAnswer();
            newAnswerObject.setAnswer(null);
            newAnswerObject.setQuestionId(questionId);
            newAnswerObject.setRequestId(requestId);
            newAnswerObject.setSentiment(null);
            returnedAnswer = feedbackAnswerServices.save(newAnswerObject);
        } else {
            returnedAnswer = list.get(0);
        }
        return new Tuple(question, returnedAnswer);
    }

    public boolean getIsPermitted(FeedbackRequest feedbackRequest) {
        final boolean isAdmin = currentUserServices.isAdmin();
        final UUID requestCreatorId = feedbackRequest.getCreatorId();
        UUID requesteeId = feedbackRequest.getRequesteeId();
        MemberProfile requestee = memberProfileServices.getById(requesteeId);
        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID recipientId = feedbackRequest.getRecipientId();
        boolean isRequesteesSupervisor = requesteeId != null ? memberProfileServices.getSupervisorsForId(requesteeId).stream().anyMatch(profile -> currentUserId.equals(profile.getId())) : false;
        final UUID requesteePDL = requestee.getPdlId();

        return isAdmin || currentUserId.equals(requesteePDL) || isRequesteesSupervisor || requestCreatorId.equals(currentUserId) || recipientId.equals(currentUserId);
    }

}
