package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.NotNull;
import java.util.*;

@Singleton
public class ReviewAssignmentServicesImpl implements ReviewAssignmentServices {

    ReviewAssignmentRepository reviewAssignmentRepository;

    MemberProfileRepository memberProfileRepository;

    private static final Logger LOG = LoggerFactory.getLogger(ReviewAssignmentServicesImpl.class);

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

    // Now that uniqueness constraints have been placed on the
    // reviewee-reviewer-reviewPeriod, this method needs to be synchronized
    // to avoid multiple calls from the client-side overlapping and attempting
    // to create the same review assignments multiple times.
    @Override
    public synchronized List<ReviewAssignment> saveAll(UUID reviewPeriodId, List<ReviewAssignment> reviewAssignments, boolean deleteExisting) {

        if(deleteExisting) {
            LOG.warn("Deleting all review assignments for review period {}", reviewPeriodId);
            reviewAssignmentRepository.deleteByReviewPeriodId(reviewPeriodId);
        }

        List<ReviewAssignment> newAssignments = new ArrayList<>();
        if (reviewAssignments != null && !reviewAssignments.isEmpty()) {
            for (ReviewAssignment reviewAssignment : reviewAssignments) {
                if (reviewAssignment.getId() != null) {
                    throw new BadArgException(String.format("Found unexpected id %s for review assignment. New entities must not contain an id.",
                        reviewAssignment.getId()));
                }
                reviewAssignment.setReviewPeriodId(reviewPeriodId);
            }

            newAssignments.addAll(reviewAssignmentRepository.saveAll(reviewAssignments));
        }
        return newAssignments;
    }

    @Override
    public ReviewAssignment findById(@NotNull UUID id) {
        return reviewAssignmentRepository.findById(id).orElse(null);
    }


    @Override
    public Set<ReviewAssignment> findAllByReviewPeriodIdAndReviewerId(UUID reviewPeriodId, @Nullable UUID reviewerId) {

        Set<ReviewAssignment> reviewAssignments;

        if (reviewerId == null) {
            reviewAssignments = reviewAssignmentRepository.findByReviewPeriodId(reviewPeriodId);
        } else {
            reviewAssignments = reviewAssignmentRepository.findByReviewPeriodIdAndReviewerId(reviewPeriodId, reviewerId);
        }

        return reviewAssignments;
    }

    @Override
    public ReviewAssignment update(ReviewAssignment reviewAssignment) {
        LOG.info("Updating entity {}", reviewAssignment);
        if (reviewAssignment.getId() != null && reviewAssignmentRepository.findById(reviewAssignment.getId()).isPresent()) {
            return reviewAssignmentRepository.update(reviewAssignment);
        } else {
            throw new BadArgException(String.format("ReviewAssignment %s does not exist, cannot update", reviewAssignment.getId()));
        }
    }

    @Override
    public void delete(UUID id) {
        if (id != null && reviewAssignmentRepository.findById(id).isPresent()) {
            reviewAssignmentRepository.deleteById(id);
        } else {
            throw new BadArgException(String.format("ReviewAssignment %s does not exist, cannot delete", id));
        }
    }

    public Set<ReviewAssignment> defaultReviewAssignments(UUID reviewPeriodId) {
        Set<ReviewAssignment> reviewAssignments = new HashSet<>();

        memberProfileRepository.findAll().forEach(memberProfile -> {
            if(memberProfile.getSupervisorid() == null) {
                return;
            }

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
