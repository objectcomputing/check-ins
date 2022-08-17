package com.objectcomputing.checkins.services.education;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.objectcomputing.checkins.exceptions.NotFoundException;

import jakarta.inject.Singleton;

@Singleton
public class EducationServicesImpl implements EducationServices {

    private final EducationRepository educationRepository;

    public EducationServicesImpl(EducationRepository educationRepository) {
        this.educationRepository = educationRepository;
    }

    @Override
    public Education getById(UUID id) {
        Optional<Education> education = educationRepository.findById(id);
        if (education.isEmpty()) {
            throw new NotFoundException("No education found for id " + id);
        }
        return education.get();
    }

    @Override
    public Set<Education> findByValues(UUID id, String highestDegree, String institution, String location,
            String degree, String major, LocalDate completionDate, String additionalInfo) {
        HashSet<Education> education = new HashSet<>(educationRepository.search(id, highestDegree, institution,
                location, degree, major, completionDate, additionalInfo));
        return education;
    }

	@Override
	public Education saveEducation(Education education) {
        if(education.getId() == null) {
            return educationRepository.save(education);
        }
        return educationRepository.update(education);
	}

	@Override
	public Boolean deleteEducation(UUID id) {
		educationRepository.deleteById(id);
		return true;
	}

}
