package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class ReviewAssignmentServicesImpl implements ReviewAssignmentServices {

    ReviewAssignmentRepository reviewAssignmentRepository;

    MemberProfileRepository memberProfileRepository;

    public ReviewAssignmentServicesImpl(ReviewAssignmentRepository reviewAssignmentRepository, MemberProfileRepository memberProfileRepository) {
        this.reviewAssignmentRepository = reviewAssignmentRepository;
        this.memberProfileRepository = memberProfileRepository;
    }

    @Override
    public ReviewAssignment save(ReviewAssignment reviewAssignment) {
        ReviewAssignment newAssignment = null;
        if (reviewAssignment != null) {

            if (reviewAssignment.getId() != null) {
                throw new BadArgException(String.format("Found unexpected id %s for review assignment. New entities must not contain an id.",
                    reviewAssignment.getId()));
            }

            //The service creates a new review assignment with an initial approved status of false.
            reviewAssignment.setApproved(false);

            newAssignment = reviewAssignmentRepository.save(reviewAssignment);
        }

        return newAssignment;
    }

    @Override
    public ReviewAssignment findById(@NotNull UUID id) {
        return reviewAssignmentRepository.findById(id).orElse(null);
    }


    @Override
    public Set<ReviewAssignment> findAllByReviewPeriodIdAndReviewerId(UUID reviewPeriodId, @Nullable UUID reviewerId) {

        Set<ReviewAssignment> reviewAssignments = null;

        if (reviewerId == null) {
            reviewAssignments = reviewAssignmentRepository.findByReviewPeriodId(reviewPeriodId);
        } else {
            reviewAssignments = reviewAssignmentRepository.findByReviewPeriodIdAndReviewerId(reviewPeriodId, reviewerId);
        }
        if (reviewAssignments.isEmpty()) {
            //If no assignments exist for the review period, then a set of default review assignments should be returned
            reviewAssignments = defaultReviewAssignments(reviewPeriodId);
        }

        return reviewAssignments;
    }


    public Set<ReviewAssignment> defaultReviewAssignments(UUID reviewPeriodId) {
        Set<ReviewAssignment> reviewAssignments = new HashSet<>();

        memberProfileRepository.findAll().forEach(memberProfile -> {
            ReviewAssignment reviewAssignment = new ReviewAssignment();
            reviewAssignment.setReviewerId(memberProfile.getSupervisorid());
            reviewAssignment.setRevieweeId(memberProfile.getId());
            reviewAssignment.setReviewPeriodId(reviewPeriodId);
            reviewAssignment.setApproved(false);

            reviewAssignments.add(reviewAssignment);
        });

        return reviewAssignments;
    }
}
