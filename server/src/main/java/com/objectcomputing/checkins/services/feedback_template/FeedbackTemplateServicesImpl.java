package com.objectcomputing.checkins.services.feedback_template;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.Util;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static com.objectcomputing.checkins.util.Validation.validate;

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

       validate(feedbackTemplate.getId() != null).orElseThrow(() -> {
           throw new BadArgException("Attempted to update template with null ID");
       });

        Optional<FeedbackTemplate> originalTemplate = feedbackTemplateRepository.findById(feedbackTemplate.getId());
        validate(originalTemplate.isPresent()).orElseThrow(() -> {
            throw new NotFoundException("Could not update template with nonexistent ID %s", feedbackTemplate.getId());
        });

        feedbackTemplate.setCreatorId(originalTemplate.get().getCreatorId());
        feedbackTemplate.setTitle(originalTemplate.get().getTitle());
        feedbackTemplate.setDescription(originalTemplate.get().getDescription());
        feedbackTemplate.setDateCreated(originalTemplate.get().getDateCreated());
        feedbackTemplate.setIsPublic(originalTemplate.get().getIsPublic());
        feedbackTemplate.setIsAdHoc(originalTemplate.get().getIsAdHoc());

        if (!updateIsPermitted(originalTemplate.get().getCreatorId())) {
            throw new PermissionException("You are not authorized to do this operation");
        }

        return feedbackTemplateRepository.update(feedbackTemplate);
    }

    @Override
    public Boolean delete(UUID id) {
        final FeedbackTemplate template = getById(id);

        validate(deleteIsPermitted(template.getCreatorId())).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

        // delete the template itself
        feedbackTemplateRepository.softDeleteById(Util.nullSafeUUIDToString(id));
        return true;
    }

    @Override
    public FeedbackTemplate getById(UUID id) {
        return feedbackTemplateRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("No feedback template with ID %s", id);
        });
    }

    @Override
    public List<FeedbackTemplate> findByFields(@Nullable UUID creatorId, @Nullable String title) {
        UUID currentUserId = currentUserServices.getCurrentUser().getId();
        boolean isAdmin = currentUserServices.isAdmin();
        List <FeedbackTemplate> allTemplates =  feedbackTemplateRepository.searchByValues(Util.nullSafeUUIDToString(creatorId), title);
        return allTemplates
                .stream()
                .filter(template -> template.getIsPublic() || isAdmin || template.getCreatorId().equals(currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean setAdHocInactiveByCreator(@Nullable UUID creatorId) {
        validate(updateIsPermitted(creatorId)).orElseThrow(() -> {
            throw new PermissionException("You are not authorized to do this operation");
        });

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
