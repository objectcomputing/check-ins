package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class FeedbackTemplateServicesImpl implements FeedbackTemplateServices {

    private final FeedbackTemplateRepository feedbackTemplateRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;
    private final Logger LOG = LoggerFactory.getLogger(FeedbackTemplateServicesImpl.class);

    public FeedbackTemplateServicesImpl(FeedbackTemplateRepository feedbackTemplateRepository,
                                        MemberProfileServices memberProfileServices,
                                        CurrentUserServices currentUserServices) {
        this.feedbackTemplateRepository = feedbackTemplateRepository;
        this.currentUserServices = currentUserServices;
        this.memberProfileServices = memberProfileServices;
    }

    @Override
    public FeedbackTemplate save(FeedbackTemplate feedbackTemplate) {
        try {
            memberProfileServices.getById(feedbackTemplate.getCreatorId());
        } catch (NotFoundException e) {
            throw new BadArgException("Creator ID is invalid");
        }

        return feedbackTemplateRepository.save(feedbackTemplate);
    }

    @Override
    public FeedbackTemplate update(FeedbackTemplate feedbackTemplate) {

       if (feedbackTemplate.getId() == null) {
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
        feedbackTemplate.setIsAdHoc(originalTemplate.get().getIsAdHoc());

        if (!updateIsPermitted(originalTemplate.get().getCreatorId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.update(feedbackTemplate);
    }

    @Override
    public Boolean delete(UUID id) {
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

        return feedbackTemplate.get();
    }

    @Override
    public List<FeedbackTemplate> findByFields(@Nullable UUID creatorId, @Nullable String title) {
        return feedbackTemplateRepository.searchByValues(Util.nullSafeUUIDToString(creatorId), title);
    }

    @Override
    public Boolean setAdHocInactiveByCreator(@Nullable UUID creatorId) {
        LOG.info("called set ad hoc inactive");
        if (!updateIsPermitted(creatorId)) {
            LOG.info("made it into throw check");
            throw new PermissionException("You are not authorized to do this operation");
        }
        LOG.info("soft delete ad hoc now going to happen :) ");
        feedbackTemplateRepository.setAdHocInactiveByCreator(Util.nullSafeUUIDToString(creatorId));
        return true;
    }

    public boolean updateIsPermitted(UUID creatorId) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        return isAdmin || currentUserId.equals(creatorId);
    }


    public boolean deleteIsPermitted(UUID creatorId) {
        return updateIsPermitted(creatorId);
    }

}
