package com.objectcomputing.checkins.services.employmenthistory;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.exceptions.NotFoundException;

import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;

@Singleton
public class EmploymentHistoryServicesImpl implements EmploymentHistoryServices {

    private final EmploymentHistoryRepository employmentHistoryRepository;

    public EmploymentHistoryServicesImpl(EmploymentHistoryRepository employmentHistoryRepository) {
        this.employmentHistoryRepository = employmentHistoryRepository;
    }

    @Override
    public EmploymentHistory getById(@NotNull UUID id) {
        Optional<EmploymentHistory> employmentHistory = employmentHistoryRepository.findById(id);
        if (employmentHistory.isEmpty()) {
            throw new NotFoundException("No onboardee profile found for id " + id);
        }
        return employmentHistory.get();
    }

    public Set<EmploymentHistory> findByValues(
            @Nullable UUID id,
            @Nullable String company,
            @Nullable String companyAddress,
            @Nullable String jobTitle,
            @Nullable LocalDate startDate,
            @Nullable LocalDate endDate,
            @Nullable String reason) {
        HashSet<EmploymentHistory> employment_history = new HashSet<>(
                employmentHistoryRepository.search(id, company, companyAddress,
                        jobTitle, startDate, endDate, reason));

        return employment_history;
    }

}