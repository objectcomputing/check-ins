package com.objectcomputing.checkins.services.questions;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.Validation.validate;

@Singleton
public class QuestionServicesImpl implements QuestionServices {

    private final QuestionRepository questionRepository;

    public QuestionServicesImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {
        Set<Question> returnedList = findByValue(question.getText());

        validate(returnedList.isEmpty()).orElseThrow(() -> {
            throw new AlreadyExistsException("Already exists");
        });

        return questionRepository.save(question);
    }

    public Set<Question> readAllQuestions() {
        return questionRepository.findAll();
    }

    public Question findById(@NotNull UUID id) {
        return questionRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(String.format("No question for id %s", id));
        });
    }

    protected Set<Question> findByValue(String text) {
        Set<Question> questionList = questionRepository.findAll();
        if (text != null) {
            questionList.retainAll(findByText(text));
        }

        return questionList;
    }

    public Set<Question> findByText(String text) {
        String wildcard = "%" + text + "%";
        return questionRepository.findByTextIlike(wildcard);
    }

    public Question update(Question question) {
        try {
            findById(question.getId());
        } catch (NotFoundException qnfe) {
            throw new BadArgException("No question found for this id");
        }

        return questionRepository.update(question);
    }

    public Set<Question> findByCategoryId(@NotNull UUID categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

}
