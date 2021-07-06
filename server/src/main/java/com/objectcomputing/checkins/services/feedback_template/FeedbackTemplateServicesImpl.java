package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FeedbackTemplateServicesImpl implements FeedbackTemplateServices {

    private final FeedbackTemplateRepository feedbackTemplateRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

    public FeedbackTemplateServicesImpl(FeedbackTemplateRepository feedbackTemplateRepository,
                                        MemberProfileServices memberProfileServices, CurrentUserServices currentUserServices) {
        this.feedbackTemplateRepository = feedbackTemplateRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public FeedbackTemplate save(FeedbackTemplate feedbackTemplate) {
        try {
            memberProfileServices.getById(feedbackTemplate.getCreatedBy());
        } catch (NotFoundException e) {
            throw new BadArgException("Creator ID is invalid");
        }

        if (!createIsPermitted()){
            throw new PermissionException("You are not authorized to do this operation");
        }
        if (feedbackTemplate.getId() != null) {
            throw new BadArgException("Attempted to save feedback template with duplicate ID");
        }

        return feedbackTemplateRepository.save(feedbackTemplate);
    }


    @Override
    public FeedbackTemplate update(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplate updatedFeedbackTemplate;

        if (feedbackTemplate.getId() != null) {
            updatedFeedbackTemplate = getById(feedbackTemplate.getId());
        } else {
            throw new BadArgException("Feedback template does not exist. Cannot update");
        }
        feedbackTemplate.setCreatedBy(updatedFeedbackTemplate.getCreatedBy());
        if (!updateIsPermitted(feedbackTemplate.getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.update(feedbackTemplate);
    }

    @Override
    public FeedbackTemplate delete(@NotNull UUID id) {
        final Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepository.findById(id);
        final String idString = Util.nullSafeUUIDToString(id);
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (!feedbackTemplate.isPresent()) {
            throw new NotFoundException("No feedback template with id " + id);
        }

        UUID creatorId = feedbackTemplate.get().getCreatedBy();
        if (!currentUserId.equals(creatorId)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        feedbackTemplateRepository.softDeleteById(id);

        return feedbackTemplate.get();
    }

    @Override
    public FeedbackTemplate getById(UUID id) {
        final Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepository.findById(id);
        if (!feedbackTemplate.isPresent()) {
            throw new NotFoundException("No feedback template with id " + id);
        }

        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplate.get();
    }


    @Override
    public List<FeedbackTemplate> findByFields(@Nullable UUID createdBy, @Nullable String title, @Nullable Boolean onlyActive) {
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        List<FeedbackTemplate> templateList = new ArrayList<>();

        // Filters only active templates by default
        if (onlyActive != null && onlyActive) {
            templateList.addAll(feedbackTemplateRepository.findByActive(true));
            if (title != null) {
                templateList.retainAll(findByTitleLike(title));
            }
            if (createdBy != null) {
                templateList.retainAll(feedbackTemplateRepository.findByCreatedBy(createdBy));
            }
        } else {
            if (title != null) {
                templateList.addAll(findByTitleLike(title));
                if (createdBy != null) {
                    templateList.retainAll(feedbackTemplateRepository.findByCreatedBy(createdBy));
                }
            } else if (createdBy != null) {
                templateList.addAll(feedbackTemplateRepository.findByCreatedBy(createdBy));
            } else {
                feedbackTemplateRepository.findAll().forEach(templateList::add);
            }
        }

        return templateList;
    }

    protected List<FeedbackTemplate> findByTitleLike(String title) {
        String wildcard = "%" + title + "%";
        return feedbackTemplateRepository.findByTitleLike(wildcard);
    }

    public boolean createIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }

    public boolean updateIsPermitted(UUID createdBy) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        return isAdmin || currentUserId.equals(createdBy);
    }

    public boolean getIsPermitted() {
        return createIsPermitted();
    }

}
