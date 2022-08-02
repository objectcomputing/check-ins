package com.objectcomputing.checkins.services.question_category;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;

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

        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource");
        });
        validate(questionCategory.getId() == null).orElseThrow(() -> {
            throw new BadArgException("Found unexpected id %s for category, please try updating instead.", questionCategory.getId());
        });
        validate(questionCategoryRepository.findByName(questionCategory.getName()).isEmpty()).orElseThrow(() -> {
            throw new AlreadyExistsException("Category %s already exists. ", questionCategory.getName());
        });

        return questionCategoryRepository.save(questionCategory);
    }

    @Override
    public QuestionCategory findById(@NotNull UUID id) {
        return questionCategoryRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException(String.format("No question category for id %s", id));
        });
    }

    @Override
    public QuestionCategory findByName(String name) {
        validate(name != null).orElseThrow(() -> {
            throw new BadArgException("Name must not be null");
        });

        return questionCategoryRepository.findByName(name).orElseThrow(() -> {
            throw new NotFoundException(String.format("No question category for name %s", name));
        });
    }

    @Override
    public QuestionCategory update(@NotNull QuestionCategory questionCategory) {
        QuestionCategory updatedQuestionCategory = null;
        if (questionCategory.getId() != null) {
            updatedQuestionCategory = getById(questionCategory.getId());
        }

        validate(updatedQuestionCategory != null).orElseThrow(() -> {
            throw new BadArgException("This question category does not exist");
        });
        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource");
        });

        return questionCategoryRepository.update(questionCategory);
    }

    @Override
    public Boolean delete(@NotNull UUID id) {
        final QuestionCategory questionCategory = questionCategoryRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No question category with id " + id);
        });
        validate(currentUserServices.isAdmin()).orElseThrow(() -> {
            throw new PermissionException("You do not have permission to access this resource");
        });

        questionCategoryRepository.deleteById(id);
        return true;
    }

    protected QuestionCategory getById(@NotNull UUID id) {
        return questionCategoryRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No category with id %s", id);
        });
    }
}
