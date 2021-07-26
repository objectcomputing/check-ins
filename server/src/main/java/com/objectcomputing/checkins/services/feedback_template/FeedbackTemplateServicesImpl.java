package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.feedback_template.template_question.*;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class FeedbackTemplateServicesImpl implements FeedbackTemplateServices {

    private final FeedbackTemplateRepository feedbackTemplateRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final TemplateQuestionServices templateQuestionServices;

    public FeedbackTemplateServicesImpl(FeedbackTemplateRepository feedbackTemplateRepository,
                                        MemberProfileServices memberProfileServices,
                                        CurrentUserServices currentUserServices,
                                        TemplateQuestionServices templateQuestionServices) {
        this.feedbackTemplateRepository = feedbackTemplateRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
        this.templateQuestionServices = templateQuestionServices;
    }

    @Override
    public FeedbackTemplate save(FeedbackTemplate feedbackTemplate) {

        if (feedbackTemplate == null) {
            throw new BadArgException("Feedback template object is null and cannot be saved");
        } else if (feedbackTemplate.getId() != null) {
            throw new BadArgException("Attempted to save template with non-auto-populated ID");
        }

        try {
            memberProfileServices.getById(feedbackTemplate.getCreatorId());
        } catch (NotFoundException e) {
            throw new BadArgException("Creator ID is invalid");
        }

        if (!createIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.save(feedbackTemplate);
    }

    @Override
    public FeedbackTemplate update(FeedbackTemplate feedbackTemplate) {

        if (feedbackTemplate == null) {
            throw new BadArgException("Feedback template object is null and cannot be updated");
        } else if (feedbackTemplate.getId() == null) {
            throw new BadArgException("Attempted to update template with null ID");
        }

        Optional<FeedbackTemplate> originalTemplate = feedbackTemplateRepository.findById(feedbackTemplate.getId());
        if (originalTemplate.isEmpty()) {
            throw new NotFoundException("Could not update template with nonexistent ID " + feedbackTemplate.getId());
        }

        feedbackTemplate.setCreatorId(originalTemplate.get().getCreatorId());
        feedbackTemplate.setTitle(originalTemplate.get().getTitle());
        feedbackTemplate.setDescription(originalTemplate.get().getDescription());
        feedbackTemplate.setDateCreated(originalTemplate.get().getDateCreated());

        if (!updateIsPermitted(originalTemplate.get().getCreatorId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.update(feedbackTemplate);
    }

    @Override
    public Boolean delete(@NotNull UUID id) {
        final FeedbackTemplate template = getById(id);

        if (!deleteIsPermitted(template.getCreatorId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        // delete the template itself
        feedbackTemplateRepository.softDeleteById(Util.nullSafeUUIDToString(id));
        return true;
    }

    @Override
    public FeedbackTemplate getById(UUID id) {
        final Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepository.findById(id);
        if (feedbackTemplate.isEmpty()) {
            throw new NotFoundException("No feedback template with ID " + id);
        }

        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplate.get();
    }

    @Override
    public List<FeedbackTemplate> findByFields(@Nullable UUID creatorId, @Nullable String title) {
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        List<FeedbackTemplate> templateList = new ArrayList<>();

        if (title != null) {
            templateList.addAll(findByTitleLike(title));
            if (creatorId != null) {
                templateList.retainAll(feedbackTemplateRepository.findByCreatorIdAndActive(creatorId, true));
            }
        } else if (creatorId != null) {
            templateList.addAll(feedbackTemplateRepository.findByCreatorIdAndActive(creatorId, true));
        } else {
            feedbackTemplateRepository.findAll().forEach(templateList::add);
        }
        return templateList;
    }

    protected List<FeedbackTemplate> findByTitleLike(String title) {
        String wildcard = "%" + title + "%";
        return feedbackTemplateRepository.findByTitleLikeAndActive(wildcard, true);
    }

    public boolean createIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }

    public boolean updateIsPermitted(UUID creatorId) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        return isAdmin || currentUserId.equals(creatorId);
    }

    public boolean getIsPermitted() {
        return createIsPermitted();
    }

    public boolean deleteIsPermitted(UUID creatorId) {
        return updateIsPermitted(creatorId);
    }

}
