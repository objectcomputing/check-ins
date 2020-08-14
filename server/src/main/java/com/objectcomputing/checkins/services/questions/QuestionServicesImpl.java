package com.objectcomputing.checkins.services.questions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.UUID;

@Singleton
public class QuestionServicesImpl implements QuestionServices {

    @Inject
    private QuestionRepository questionRepository;

    public void setQuestionRepository(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {

        List<Question> returnedList = findByValue(question.getText());
        if (returnedList.size() >= 1) {
            throw new QuestionDuplicateException("Already exists");
        }
        return questionRepository.save(question);

    }

    public List<Question> readAllQuestions() {
        List<Question> questionList = questionRepository.findAll();

        return questionList;

    }

    public Question findByQuestionId(UUID skillId) {

        Question returned = questionRepository.findByQuestionid(skillId)
                .orElseThrow(() -> new QuestionNotFoundException("No question for uuid"));

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
        try {
            findByQuestionId(question.getQuestionid());
        } catch (QuestionNotFoundException qnfe) {
            throw new QuestionBadArgException("No question found for this uuid");
        }
        returned = questionRepository.update(question);

        return returned;

    }

}
