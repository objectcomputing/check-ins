package com.objectcomputing.checkins.services.employmenthistory;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import jakarta.inject.Singleton;

@Singleton
public class EmploymentHistoryServicesImpl implements EmploymentHistoryServices {

	@Override
	public EmploymentHistory getById(@NotNull UUID id) {
        return null;
	}
    
}
