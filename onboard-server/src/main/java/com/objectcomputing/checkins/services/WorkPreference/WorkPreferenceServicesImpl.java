package com.objectcomputing.checkins.services.WorkPreference;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import io.micronaut.core.annotation.Nullable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;

public class WorkPreferenceServicesImpl implements WorkPreferenceServices {

    private final WorkPreferenceRepository workPreferenceRepository;

    public WorkPreferenceServicesImpl(WorkPreferenceRepository workPreferenceRepository) {
        this.workPreferenceRepository = workPreferenceRepository;
    }

    @Override
    public WorkPreference getById(@NotNull UUID id) {
        Optional<WorkPreference> workPreference = workPreferenceRepository.findById(id);
        if (workPreference.isEmpty()) {
            throw new NotFoundException("No new employee profile for id " + id);
        }
        return workPreference.get();
    }

    @Override
    public Set<WorkPreference> findByValues (
            @Nullable UUID id,
            @Nullable String desiredPosition,
            @Nullable LocalDate desiredStartDate,
            @Nullable Boolean currentlyEmployed,
            @Nullable String referredBy,
            @Nullable String referrerEmail) {
        HashSet<WorkPreference> work_preferences = new HashSet<>(workPreferenceRepository.search( (nullSafeUUIDToString(id)), desiredPosition, desiredStartDate,
                currentlyEmployed, referredBy, referrerEmail));
        return work_preferences;
    }

    @Override
    public WorkPreference savePreferences(WorkPreference work_preferences) {
        if (work_preferences.getId() == null) {
            return workPreferenceRepository.save(work_preferences);
        }
        return workPreferenceRepository.update(work_preferences);
    }

    @Override
    public Boolean deletePreferences(@NotNull UUID id) {
        workPreferenceRepository.deleteById(id);
        return true;
    }

    @Override
    public WorkPreference findByPosition(String desiredPosition) {
        List<WorkPreference> searchResult = workPreferenceRepository.search(null, desiredPosition, null, null, null, null);
        if (searchResult.size() != 1) {
            throw new BadArgException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }

    @Override
    public WorkPreference findByReferral(String referredBy) {
        List<WorkPreference> searchResult = workPreferenceRepository.search(null, null, null, null, referredBy, null);
        if (searchResult.size() != 1) {
            throw new BadArgException("Expected exactly 1 result. Found " + searchResult.size());
        }
        return searchResult.get(0);
    }

    @Override
    public List<WorkPreference> findAll() { return workPreferenceRepository.findAll(); }

}
