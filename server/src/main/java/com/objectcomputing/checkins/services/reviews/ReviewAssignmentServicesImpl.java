package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import jakarta.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Singleton
public class ReviewAssignmentServicesImpl implements ReviewAssignmentServices {

    ReviewAssignmentRepository reviewAssignmentRepository;

    public ReviewAssignmentServicesImpl(ReviewAssignmentRepository reviewAssignmentRepository) {
        this.reviewAssignmentRepository = reviewAssignmentRepository;
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
}
