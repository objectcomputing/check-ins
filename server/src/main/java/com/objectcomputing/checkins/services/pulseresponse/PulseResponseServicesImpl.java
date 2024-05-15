package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.exceptions.BadArgException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        if(pulseResponse!=null){
            final UUID memberId = pulseResponse.getTeamMemberId();
            LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
            LocalDate pulseUpDate = pulseResponse.getUpdatedDate();
            if(pulseResponse.getId()!=null){
            throw new BadArgException(String.format("Found unexpected id for pulseresponse %s", pulseResponse.getId()));
        } else if(!memberRepo.findById(memberId).isPresent()){
            throw new BadArgException(String.format("Member %s doesn't exists", memberId));
        } else if(pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
            throw new BadArgException(String.format("Invalid date for pulseresponse submission date %s",memberId));
        } else if(pulseUpDate.isBefore(LocalDate.EPOCH) || pulseUpDate.isAfter(LocalDate.MAX)) {
            throw new BadArgException(String.format("Invalid date for pulseresponse updated date %s",memberId));
        }
        pulseResponseRet = pulseResponseRepo.save(pulseResponse);
        }
            return pulseResponseRet ;
    }


    @Override
    public PulseResponse read(@NotNull UUID id) {
        return pulseResponseRepo.findById(id).orElse(null);
    }

    @Override
    public PulseResponse update(PulseResponse pulseResponse) {
        PulseResponse pulseResponseRet = null;
        if(pulseResponse!=null){
        final UUID id = pulseResponse.getId();
        final UUID memberId = pulseResponse.getTeamMemberId();
        LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
        LocalDate pulseUpDate = pulseResponse.getUpdatedDate();
        if(id==null||!pulseResponseRepo.findById(id).isPresent()){
            throw new BadArgException(String.format("Unable to find pulseresponse record with id %s", pulseResponse.getId()));
        }else if(!memberRepo.findById(memberId).isPresent()){
            throw new BadArgException(String.format("Member %s doesn't exist", memberId));
        } else if(memberId==null) {
            throw new BadArgException(String.format("Invalid pulseresponse %s", pulseResponse));
        } else if(pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
            throw new BadArgException(String.format("Invalid date for pulseresponse submission date %s",memberId));
        } else if(pulseUpDate.isBefore(LocalDate.EPOCH) || pulseUpDate.isAfter(LocalDate.MAX)) {
            throw new BadArgException(String.format("Invalid date for pulseresponse %s",memberId));
        }

        pulseResponseRet = pulseResponseRepo.update(pulseResponse);
        }        
        return pulseResponseRet;
    }

    @Override
    public Set<PulseResponse> findByFields(UUID teamMemberId, LocalDate dateFrom, LocalDate dateTo) {
        Set<PulseResponse> pulseResponse = new HashSet<>();
        pulseResponseRepo.findAll().forEach(pulseResponse::add);
        if(teamMemberId!=null){
            pulseResponse.retainAll(pulseResponseRepo.findByTeamMemberId(teamMemberId));
        } else if(dateFrom!=null && dateTo!=null) {
            pulseResponse.retainAll(pulseResponseRepo.findBySubmissionDateBetween(dateFrom, dateTo));
        }
        return pulseResponse;
    }   
}