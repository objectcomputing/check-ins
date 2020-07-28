package com.objectcomputing.checkins.services.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class QuestionServices {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionServices.class);

    @Inject
    private QuestionRepository questionRepository;

    public void setQuestionRepository(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {

        List<Question> returnedList = findByValue(null, question.getText());
        return returnedList.size() < 1 ? questionRepository.save(question) : null;

    }

    protected Question findByQuestionId(UUID skillId) {

        Question returned = questionRepository.findByQuestionid(skillId);

        return returned;

    }

    protected List<Question> findByValue(UUID questionId, String text) {
        List<Question> questionList = null;
        if(text != null) {
            questionList = findByText(text);
        } else {
  //          readAllQuestions();
        }

        return questionList;
    }

    private List<Question> findByText(String text) {
        List<Question> skillList = questionRepository.findByText(text);

        return skillList;
    }

    public Question update(Question question) {
        Question returned = null;
        Question questionInDatabase = findByQuestionId(question.getQuestionid());
        if ((questionInDatabase != null)
                && questionInDatabase.getText().equals(question.getText())) {
            returned = questionRepository.update(question);
        }

        return returned;

    }

}
