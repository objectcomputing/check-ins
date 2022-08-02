package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import jakarta.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.util.Validation.validate;

@Singleton
public class PulseResponseServicesImpl implements PulseResponseService {

    private final PulseResponseRepository pulseResponseRepo;
    private final MemberProfileRepository memberRepo;

    public PulseResponseServicesImpl(PulseResponseRepository pulseResponseRepo,
                                     MemberProfileRepository memberRepo) {
        this.pulseResponseRepo = pulseResponseRepo;
        this.memberRepo = memberRepo;
    }
    
    @Override
    public PulseResponse save(PulseResponse pulseResponse) {
        PulseResponse pulseResponseRet = null;
        if (pulseResponse != null) {
            final UUID memberId = pulseResponse.getTeamMemberId();
            LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
            LocalDate pulseUpDate = pulseResponse.getUpdatedDate();

            validate(pulseResponse.getId() == null).orElseThrow(() -> {
                throw new BadArgException("Found unexpected id for pulseresponse %s", pulseResponse.getId());
            });
            validate(memberRepo.findById(memberId).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exists", memberId);
            });
            validate(pulseSubDate.isAfter(LocalDate.EPOCH) && pulseSubDate.isBefore(LocalDate.MAX)).orElseThrow(() -> {
                throw new BadArgException("Invalid date for pulseresponse submission date %s",memberId);
            });
            validate(pulseUpDate.isAfter(LocalDate.EPOCH) && pulseUpDate.isBefore(LocalDate.MAX)).orElseThrow(() -> {
                throw new BadArgException("Invalid date for pulseresponse updated date %s",memberId);
            });

            pulseResponseRet = pulseResponseRepo.save(pulseResponse);
        }

        return pulseResponseRet;
    }


    @Override
    public PulseResponse read(@NotNull UUID id) {
        return pulseResponseRepo.findById(id).orElse(null);
    }

    @Override
    public PulseResponse update(PulseResponse pulseResponse) {
        PulseResponse pulseResponseRet = null;
        if (pulseResponse != null) {
            final UUID id = pulseResponse.getId();
            final UUID memberId = pulseResponse.getTeamMemberId();
            LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
            LocalDate pulseUpDate = pulseResponse.getUpdatedDate();

            validate(id != null && pulseResponseRepo.findById(id).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Unable to find pulseresponse record with id %s", pulseResponse.getId());
            });
            validate(memberRepo.findById(memberId).isPresent()).orElseThrow(() -> {
                throw new BadArgException("Member %s doesn't exist", memberId);
            });
            validate(pulseSubDate.isAfter(LocalDate.EPOCH) && pulseSubDate.isBefore(LocalDate.MAX)).orElseThrow(() -> {
                throw new BadArgException("Invalid date for pulseresponse submission date %s",memberId);
            });
            validate(pulseUpDate.isAfter(LocalDate.EPOCH) && pulseUpDate.isBefore(LocalDate.MAX)).orElseThrow(() -> {
                throw new BadArgException("Invalid date for pulseresponse %s",memberId);
            });

            pulseResponseRet = pulseResponseRepo.update(pulseResponse);
        }
        return pulseResponseRet;
    }

    @Override
    public Set<PulseResponse> findByFields(UUID teamMemberId, LocalDate dateFrom, LocalDate dateTo) {
        Set<PulseResponse> pulseResponse = new HashSet<>();
        pulseResponseRepo.findAll().forEach(pulseResponse::add);
        if (teamMemberId != null) {
            pulseResponse.retainAll(pulseResponseRepo.findByTeamMemberId(teamMemberId));
        } else if (dateFrom != null && dateTo != null) {
            pulseResponse.retainAll(pulseResponseRepo.findBySubmissionDateBetween(dateFrom, dateTo));
        }
        return pulseResponse;
    }   
}