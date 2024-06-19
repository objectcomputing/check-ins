package com.objectcomputing.checkins.services.question_category;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class QuestionCategoryServicesImpl implements QuestionCategoryServices {

    private final QuestionCategoryRepository questionCategoryRepository;
    private final CurrentUserServices currentUserServices;

    public QuestionCategoryServicesImpl(QuestionCategoryRepository questionCategoryRepository,
                                        CurrentUserServices currentUserServices) {
        this.questionCategoryRepository = questionCategoryRepository;
        this.currentUserServices = currentUserServices;
    }

    @Override
    public Set<QuestionCategory> findByValue(String name, UUID id) {
        Set<QuestionCategory> categoryList = new HashSet<>();

        if (name != null) {
            categoryList.add(findByName(name));
        } else if (id != null) {
            categoryList.add(findById(id));
        } else {
            categoryList.addAll(questionCategoryRepository.findAll());
        }

        return categoryList;
    }

    @Override
    public QuestionCategory saveQuestionCategory(QuestionCategory questionCategory) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        if (questionCategory.getId() != null) {
            throw new BadArgException(String.format("Found unexpected id %s for category, please try updating instead.",
                    questionCategory.getId()));
        } else if (questionCategoryRepository.findByName(questionCategory.getName()).isPresent()) {
            throw new AlreadyExistsException(String.format("Category %s already exists. ", questionCategory.getName()));
        }
        return questionCategoryRepository.save(questionCategory);
    }

    @Override
    public QuestionCategory findById(@NotNull UUID id) {
        return questionCategoryRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("No question category for id %s", id)));
    }

    @Override
    public QuestionCategory findByName(String name) {
        if (name != null) {
            return questionCategoryRepository.findByName(name).orElseThrow(() ->
                    new NotFoundException(String.format("No question category for name %s", name)));
        } else {
            throw new BadArgException("Name must not be null");
        }
    }

    @Override
    public QuestionCategory update(@NotNull QuestionCategory questionCategory) {
        QuestionCategory updatedQuestionCategory = null;
        if (questionCategory.getId() != null) {
            updatedQuestionCategory = getById(questionCategory.getId());
        }
        if (updatedQuestionCategory != null) {
            if (!currentUserServices.isAdmin()) {
                throw new PermissionException(NOT_AUTHORIZED_MSG);
            }
            return questionCategoryRepository.update(questionCategory);
        } else {
            throw new BadArgException("This question category does not exist");
        }
    }

    @Override
    public boolean delete(@NotNull UUID id) {
        final Optional<QuestionCategory> questionCategory = questionCategoryRepository.findById(id);
        if (questionCategory.isEmpty()) {
            throw new NotFoundException("No question category with id " + id);
        }
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
        questionCategoryRepository.deleteById(id);
        return true;
    }

    protected QuestionCategory getById(@NotNull UUID id) {
        final Optional<QuestionCategory> questionCategory = questionCategoryRepository.findById(id);
        if (questionCategory.isEmpty()) {
            throw new NotFoundException("No category with id " + id);
        }
        return questionCategory.get();
    }
}
