package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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

        if (feedbackTemplate.getId() == null) {
            return feedbackTemplateRepository.save(feedbackTemplate);
        }

        if (!isPermitted()){
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.save(feedbackTemplate);

    }

    @Override
    public List<FeedbackTemplate> findByCreatedBy(UUID createdBy){
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        LOG.info("current user id: {}:", currentUserId);
        LOG.info("creator id: {}", createdBy);
        if (!currentUserId.equals(createdBy)) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.findByCreatedBy(createdBy);

    }

    @Override
    public FeedbackTemplate update(FeedbackTemplate feedbackTemplate) {
        FeedbackTemplate updatedFeedbackTemplate = null;
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (feedbackTemplate.getId() != null) {
            updatedFeedbackTemplate = getById(feedbackTemplate.getId());
        } else {
            throw new BadArgException("Feedback template does not exist. Cannot update");
        }
        if (!currentUserId.equals(updatedFeedbackTemplate.getCreatedBy())) {
            throw new PermissionException("You are not authorized to do this operation");
        }
        feedbackTemplate.setCreatedBy(updatedFeedbackTemplate.getCreatedBy());
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

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final UUID creatorId = feedbackTemplate.get().getCreatedBy();
        final Boolean isPrivate = feedbackTemplate.get().getIsPrivate();
        if (!currentUserId.equals(creatorId) && isPrivate) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplate.get();

    }

    @Override
    public List<FeedbackTemplate> getByValues(String title) {
        final ArrayList<FeedbackTemplate> result = new ArrayList<>(feedbackTemplateRepository.searchByValues(title));

        final UUID currentUserId = currentUserServices.getCurrentUser().getId();
        final ArrayList<FeedbackTemplate> toRemove = new ArrayList<>();

        for (FeedbackTemplate feedbackTemplate : result) {
            if (feedbackTemplate.getIsPrivate() && !currentUserId.equals(feedbackTemplate.getCreatedBy())) {
                toRemove.add(feedbackTemplate);
            }
        }
        result.removeAll(toRemove);

        return result;
    }

    @Override
    public List<FeedbackTemplate> findByFields(UUID createdBy, String title) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        List<FeedbackTemplate> result = new ArrayList<>();
          final ArrayList<FeedbackTemplate> toRemove = new ArrayList<>();
        if (title != null && createdBy == null) {
            result = feedbackTemplateRepository.findByTitle(title);
            for (FeedbackTemplate feedbackTemplate : result) {
                if (feedbackTemplate.getIsPrivate() && !currentUserId.equals(feedbackTemplate.getCreatedBy())) {
                    toRemove.add(feedbackTemplate);
                }
            }
           
        } else if (createdBy != null && title == null) {
            result = feedbackTemplateRepository.findByCreatedBy(createdBy);
            for (FeedbackTemplate feedbackTemplate : result) {
                if (feedbackTemplate.getIsPrivate() && !currentUserId.equals(feedbackTemplate.getCreatedBy())) {
                    toRemove.add(feedbackTemplate);
                }
            }

        }
        result.removeAll(toRemove);
        return result;

    }

    public Boolean isPermitted() {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        if (currentUserId == null) {
            return false;
        }
        return true;
    }
}
