package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.core.annotation.Nullable;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Singleton
public class QuestionAndAnswerServicesImpl implements QuestionAndAnswerServices {
    private final FeedbackAnswerServices feedbackAnswerServices;
    private final CurrentUserServices currentUserServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final FeedbackRequestServices feedbackRequestServices;

    public QuestionAndAnswerServicesImpl(FeedbackAnswerServices feedbackAnswerServices,
                                         CurrentUserServices currentUserServices,
                                         TemplateQuestionServices templateQuestionServices,
                                         FeedbackRequestServices feedbackRequestServices) {
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.currentUserServices = currentUserServices;
        this.templateQuestionServices = templateQuestionServices;
        this.feedbackRequestServices = feedbackRequestServices;
    }

    @Override
    public List<Tuple> getAllQuestionsAndAnswers(UUID requestId) {
        FeedbackRequest feedbackRequest = feedbackRequestServices.getById(requestId);
        if (!getIsPermitted(feedbackRequest)) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        List<FeedbackAnswer> answerList = feedbackAnswerServices.findByValues(null, requestId);
        List<Tuple> returnerList = new ArrayList<>();
        if (!answerList.isEmpty()) {
            for (FeedbackAnswer answer : answerList) {
                TemplateQuestion elementQuestion = templateQuestionServices.getById(answer.getQuestionId());
                Tuple newTuple = new Tuple(elementQuestion, answer, feedbackRequest);
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
            throw new PermissionException("You are not authorized to do this operation");
        }

        if (questionId != null) {
            question = templateQuestionServices.getById(questionId);
        }

        List<FeedbackAnswer> list;
        list = feedbackAnswerServices.findByValues(questionId, requestId);

        FeedbackAnswer returnedAnswer;
        if (list.size() == 0) {
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

    public boolean getIsPermitted(FeedbackRequest request) {
        boolean isAdmin = currentUserServices.isAdmin();
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return isAdmin || currentUserId.equals(request.getCreatorId()) || currentUserId.equals(request.getRecipientId());
    }

}
