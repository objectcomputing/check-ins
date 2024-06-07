package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.reviews.ReviewAssignment;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;

import java.util.UUID;

public interface ReviewAssignmentFixture extends RepositoryFixture {


    default ReviewAssignment createADefaultReviewAssignment() {

        return getReviewAssignmentRepository().save(new ReviewAssignment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false));

    }

    default ReviewAssignment createAReviewAssignmentBetweenMembers(MemberProfile reviewee, MemberProfile reviewer, ReviewPeriod reviewPeriod, Boolean approved) {

        return getReviewAssignmentRepository().save(new ReviewAssignment(reviewee.getId(), reviewer.getId(), reviewPeriod.getId(), approved));

    }

}
