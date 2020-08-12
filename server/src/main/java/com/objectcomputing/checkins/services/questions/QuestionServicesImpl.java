package com.objectcomputing.checkins.services.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class QuestionServicesImpl implements QuestionServices {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionServicesImpl.class);

    @Inject
    private QuestionRepository questionRepository;

    public void setQuestionRepository(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {

        List<Question> returnedList = findByValue(question.getText());
        return returnedList.size() < 1 ? questionRepository.save(question) : null;

    }

    public List<Question> readAllQuestions() {
        List<Question> questionList = questionRepository.findAll();

        return questionList;

    }

    public Question findByQuestionId(UUID skillId) {

        Question returned = questionRepository.findByQuestionid(skillId);

        return returned;

    }

    protected List<Question> findByValue(String text) {
        List<Question> questionList = null;
        if(text != null) {
            questionList = findByText(text);
        }

        return questionList;
    }

    public List<Question> findByText(String text) {
        String wildcard = "%" + text + "%" ;
        List<Question> skillList = questionRepository.findByTextIlike(wildcard);

        return skillList;
    }

    public Question update(Question question) {
        Question returned = null;
        Question questionInDatabase = findByQuestionId(question.getQuestionid());
        if ((questionInDatabase != null)
                && !questionInDatabase.getText().equals(question.getText())) {
            returned = questionRepository.update(question);
        }

        return returned;

    }

}
