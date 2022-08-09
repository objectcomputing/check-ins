package com.objectcomputing.checkins.services.employmentpreferences;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.WorkPreference.WorkPreference;
import com.objectcomputing.checkins.services.WorkPreference.WorkPreferenceRepository;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

public class EmploymentDesiredAvailabilityServicesImpl {

    private final EmploymentDesiredAvailabilityRepository employmentDesiredAvailabilityRepository;

    public EmploymentDesiredAvailabilityServicesImpl(EmploymentDesiredAvailabilityRepository employmentDesiredAvailabilityRepository) {
        this.employmentDesiredAvailabilityRepository = employmentDesiredAvailabilityRepository;
    }

    @Override
    public EmploymentDesiredAvailability getById(@NotNull UUID id) {
        Optional<EmploymentDesiredAvailability> employmentDesiredAvailabilityPreferences = employmentDesiredAvailabilityRepository.findById(id);
        if (employmentDesiredAvailabilityPreferences.isEmpty()) {
            throw new NotFoundException("No new employee profile for id " + id);
        }
        return employmentDesiredAvailabilityPreferences.get();
    }

    @Override
    public Set<EmploymentDesiredAvailability> findByValues (
            @Nullable UUID id,
            @Nullable String desiredPosition,
            @Nullable LocalDate desiredStartDate,
            @Nullable Boolean currentlyEmployed) {
        HashSet<EmploymentDesiredAvailability> employed_desired_availability_preferences = new HashSet<>(employmentDesiredAvailabilityRepository.search( (nullSafeUUIDToString(id)), desiredPosition, desiredStartDate,
                currentlyEmployed));
        return employed_desired_availability_preferences;
    }

    @Override
    public EmploymentDesiredAvailability saveDesiredAvailability(EmploymentDesiredAvailability employmentDesiredAvailability) {
        if (employmentDesiredAvailability.getId() == null) {
            return employmentDesiredAvailabilityRepository.save(employmentDesiredAvailability);
        }
        return employmentDesiredAvailabilityRepository.update(employmentDesiredAvailability);
    }

    @Override
    public Boolean deletePreferences(@NotNull UUID id) {
        employmentDesiredAvailabilityRepository.deleteById(id);
        return true;
    }

    @Override
    public EmploymentDesiredAvailability findByPosition(String desiredPosition) {
        List<EmploymentDesiredAvailability> searchResult = employmentDesiredAvailabilityRepository.search(null, desiredPosition, null, null, null, null);
        if (searchResult.size() != 1) {
            throw new BadArgException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }

    @Override
    public EmploymentDesiredAvailability findByReferral(String referredBy) {
        List<EmploymentDesiredAvailability> searchResult = employmentDesiredAvailabilityRepository.search(null, null, null, null, referredBy, null);
        if (searchResult.size() != 1) {
            throw new BadArgException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }

    @Override
    public List<EmploymentDesiredAvailability> findAll() { return employmentDesiredAvailabilityRepository.findAll(); }

}
