package com.objectcomputing.checkins.services.feedback_request.has_feedback_request;

import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import io.micronaut.data.annotation.Query;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

public interface HasFeedbackRequestRepository {
    //find all by request id
    //find all open requests by recipient id
    //save
    //update
    //delete(?)
    //find by id/??
    HasFeedbackRequest save(HasFeedbackRequest hasFeedbackRequest);
    HasFeedbackRequest update(HasFeedbackRequest hasFeedbackRequest);
    List<HasFeedbackRequest> findByUserId(UUID userId);
    List<HasFeedbackRequest> findByRequestId(UUID requestId);
    Optional<HasFeedbackRequest> findByRequestIdandUserId(@NotNull UUID userId, @ NotNull UUID requestId);
    HasFeedbackRequest getById(UUID id);
    Boolean delete(UUID id);



}
