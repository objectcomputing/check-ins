package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.services.validate.PermissionsValidation.NOT_AUTHORIZED_MSG;

@Singleton
public class FeedbackTemplateServicesImpl implements FeedbackTemplateServices {

    private final FeedbackTemplateRepository feedbackTemplateRepository;
    private final CurrentUserServices currentUserServices;
    private final MemberProfileServices memberProfileServices;

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
        feedbackTemplate.setIsPublic(originalTemplate.get().getIsPublic());
        feedbackTemplate.setIsAdHoc(originalTemplate.get().getIsAdHoc());
        feedbackTemplate.setIsReview(originalTemplate.get().getIsReview());

        if (!updateIsPermitted(originalTemplate.get().getCreatorId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        return feedbackTemplateRepository.update(feedbackTemplate);
    }

    @Override
    public void delete(UUID id) {
        final FeedbackTemplate template = getById(id);

        if (!deleteIsPermitted(template.getCreatorId())) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }

        // delete the template itself
        feedbackTemplateRepository.softDeleteById(Util.nullSafeUUIDToString(id));
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
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        List <FeedbackTemplate> allTemplates =  feedbackTemplateRepository.searchByValues(Util.nullSafeUUIDToString(creatorId), title);
        return allTemplates
                .stream()
                .filter(template -> template.getIsPublic() || isAdmin || template.getCreatorId().equals(currentUserId))
                .filter(template -> !template.getIsReview() || isAdmin)
                .toList();
    }

    @Override
    public boolean setAdHocInactiveByCreator(@Nullable UUID creatorId) {
        if (!updateIsPermitted(creatorId)) {
            throw new PermissionException(NOT_AUTHORIZED_MSG);
        }
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
