package com.objectcomputing.checkins.services.feedback_answer.question_and_answer;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import javax.inject.Singleton;
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
    public Tuple getQuestionAndAnswer(UUID requestId, UUID questionId) {
        FeedbackRequest feedbackRequest;
        TemplateQuestion question;
        try {
            feedbackRequest = feedbackRequestServices.getById(requestId);
        } catch (NotFoundException e) {
            throw new BadArgException("Cannot find request with provided ID");
        }
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (!(currentUserId.equals(feedbackRequest.getCreatorId()) ||
                currentUserId.equals(feedbackRequest.getRecipientId()) || currentUserServices.isAdmin())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        try {
            question = templateQuestionServices.getById(questionId);
        } catch (NotFoundException e) {
            throw new BadArgException("Cannot find question with provided ID");
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
        }
        else {
            returnedAnswer = list.get(0);
        }

        return new Tuple(question, returnedAnswer);
    }

}
