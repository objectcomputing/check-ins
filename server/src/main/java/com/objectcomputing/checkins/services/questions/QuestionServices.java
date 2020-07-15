package com.objectcomputing.checkins.services.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuestionServices {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionServices.class);

    //    an endpoint is created for read all
    //    an endpoint is created for create
    //    an endpoint is created for update

    @Inject
    private QuestionRepository questionRepository;

    protected Question saveQuestion(Question question) {

        List<Question> returnedList = findByValue(null, question.getText());
        return returnedList.size() < 1 ? questionRepository.save(question) : null;

    }

    protected List<Question> readAllQuestions() {

        List<Question> returned = questionRepository.findAll();

        return returned;

    }

    protected Question findQuestion(UUID skillId) {

        Question returned = questionRepository.findByQuestionId(skillId);

        return returned;

    }

    protected List<Question> findByValue(UUID questionId, String text) {
        List<Question> questionList = null;
        if (questionId != null) {
            questionList = Collections.singletonList(findQuestion(questionId));
        } else if(text != null) {
            questionList = findByText(text);
        } else {
            readAllQuestions();
        }

        return questionList;
    }

    private List<Question> findByText(String text) {
        List<Question> skillList = questionRepository.findByText(text);

        return skillList;
    }

    public Question update(Question question) {
        Question returned = questionRepository.update(question);

        return returned;

    }

}
