package com.objectcomputing.checkins.services.reviews;

import com.objectcomputing.checkins.exceptions.AlreadyExistsException;
import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.PermissionException;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class ReviewPeriodServicesImpl implements ReviewPeriodServices {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewPeriodServicesImpl.class);

    private final ReviewPeriodRepository reviewPeriodRepository;
    private final CurrentUserServices currentUserServices;

    public ReviewPeriodServicesImpl(ReviewPeriodRepository reviewPeriodRepository,
                                    CurrentUserServices currentUserServices) {
        this.reviewPeriodRepository = reviewPeriodRepository;
        this.currentUserServices = currentUserServices;
    }

    public ReviewPeriod save(ReviewPeriod reviewPeriod) {
        ReviewPeriod newPeriod = null;
        if (reviewPeriod != null) {

            if (reviewPeriod.getId() != null) {
                throw new BadArgException(String.format("Found unexpected id %s for review period. New entities must not contain an id.",
                        reviewPeriod.getId()));
            } else if (reviewPeriodRepository.findByName(reviewPeriod.getName()).isPresent()) {
                throw new AlreadyExistsException(String.format("Review Period \"%s\" already exists.", reviewPeriod.getName()));
            }

            newPeriod = reviewPeriodRepository.save(reviewPeriod);
        }

        return newPeriod;
    }

    public ReviewPeriod findById(@NotNull UUID id) {
        return reviewPeriodRepository.findById(id).orElse(null);
    }

    public Set<ReviewPeriod> findByValue(String name, ReviewStatus reviewStatus) {
        Set<ReviewPeriod> reviewPeriods = new HashSet<>();

        if (name != null) {
            reviewPeriods = findByNameLike(name).stream()
                    .filter(rp -> reviewStatus == null || Objects.equals(rp.getReviewStatus(), reviewStatus))
                    .collect(Collectors.toSet());
        } else if (reviewStatus != null) {
            reviewPeriods.addAll(reviewPeriodRepository.findByReviewStatus(reviewStatus));
        } else {
            reviewPeriodRepository.findAll().forEach(reviewPeriods::add);
        }

        return reviewPeriods;
    }

    public void delete(@NotNull UUID id) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource");
        }
        reviewPeriodRepository.deleteById(id);
    }

    protected List<ReviewPeriod> findByNameLike(String name) {
        String wildcard = "%" + name + "%";
        return reviewPeriodRepository.findByNameIlike(wildcard);
    }

    public ReviewPeriod update(@NotNull ReviewPeriod reviewPeriod) {
        if (!currentUserServices.isAdmin()) {
            throw new PermissionException("You do not have permission to access this resource");
        }
        LOG.warn(String.format("Updating entity %s", reviewPeriod));
        if (reviewPeriod.getId() != null && reviewPeriodRepository.findById(reviewPeriod.getId()).isPresent()) {
            return reviewPeriodRepository.update(reviewPeriod);
        } else {
            throw new BadArgException(String.format("ReviewPeriod %s does not exist, cannot update", reviewPeriod.getId()));
        }
    }

}
