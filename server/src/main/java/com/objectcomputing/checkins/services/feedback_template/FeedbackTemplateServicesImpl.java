package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
    private static final Logger LOG = LoggerFactory.getLogger(FeedbackTemplateServices.class);

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
            LOG.info("Updated template : {} ", updatedFeedbackTemplate.toString());
            LOG.info("Original template: {} ", feedbackTemplate.toString());
        } else {
            throw new BadArgException("Feedback template does not exist. Cannot update");
        }
        feedbackTemplate.setCreatedBy(updatedFeedbackTemplate.getCreatedBy());
        LOG.info("Original template: updated: {} ", feedbackTemplate.toString());
        if (!updateIsPermitted(feedbackTemplate.getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.update(feedbackTemplate);
    }

    @Override
    public Boolean delete(@NotNull UUID id) {
        return null;
//        final Optional<FeedbackTemplate> feedbackTemplate = feedbackTemplateRepository.findById(id);
//        UUID currentUserId = currentUserServices.getCurrentUser().getId();
//        if (!feedbackTemplate.isPresent()) {
//            throw new NotFoundException("No feedback template with id " + id);
//        }
//
//        UUID creatorId = feedbackTemplate.get().getCreatedBy();
//        if (!currentUserId.equals(creatorId)) {
//            throw new PermissionException("You are not authorized to do this operation");
//        }
//
//        feedbackTemplateRepository.deleteById(id);
//        return true;
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
    public List<FeedbackTemplate> findByFields(@Nullable UUID createdBy, @Nullable String title) {
        LOG.info("Find by field (crratedby): {}", createdBy);
        LOG.info("Find by field (title): {}", getIsPermitted());
        LOG.info(currentUserServices.getCurrentUser().getId().toString());
        if (!getIsPermitted()) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        List<FeedbackTemplate> queryResults = feedbackTemplateRepository.search(Util.nullSafeUUIDToString(createdBy), title);
        final List<FeedbackTemplate> result = new ArrayList<>(queryResults);

        return result;
    }

    public boolean createIsPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        return currentUserId != null;
    }

    public boolean updateIsPermitted(UUID createdBy) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        LOG.info("permit: {}, {}, {}", isAdmin, currentUserId, createdBy);
        return isAdmin || currentUserId.equals(createdBy);
    }

    public boolean getIsPermitted() {
        return createIsPermitted();
    }

}
