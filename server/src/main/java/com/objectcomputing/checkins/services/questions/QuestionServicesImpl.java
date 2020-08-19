package com.objectcomputing.checkins.services.questions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Singleton
public class QuestionServicesImpl implements QuestionServices {

    @Inject
    private QuestionRepository questionRepository;

    public void setQuestionRepository(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {

        Set<Question> returnedList = findByValue(question.getText());
        if (returnedList.size() >= 1) {
            throw new QuestionDuplicateException("Already exists");
        }
        return questionRepository.save(question);

    }

    public Set<Question> readAllQuestions() {
        Set<Question> questionList = questionRepository.findAll();

        return questionList;

    }

    public Question findByQuestionId(UUID skillId) {

        Question returned = questionRepository.findByQuestionid(skillId)
                .orElseThrow(() -> new QuestionNotFoundException("No question for uuid"));

        return returned;

    }

    protected Set<Question> findByValue(String text) {
        Set<Question> questionList = questionRepository.findAll();
        if(text != null) {
            questionList.retainAll(findByText(text));
        }

        return questionList;
    }

    public Set<Question> findByText(String text) {
        String wildcard = "%" + text + "%" ;
        Set<Question> skillList = questionRepository.findByTextIlike(wildcard);

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
