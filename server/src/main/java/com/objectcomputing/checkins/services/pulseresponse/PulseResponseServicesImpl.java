package com.objectcomputing.checkins.services.pulseresponse;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;


public class PulseResponseServicesImpl implements PulseResponseService {

    @Inject
    private PulseResponseRepository pulseResponseRepo;

    @Inject
    private MemberProfileRepository memberRepo;
    
    @Override
    public PulseResponse save(PulseResponse pulseResponse) {
        PulseResponse pulseResponseRet = null;
        if(pulseResponse!=null){
            final UUID memberId = pulseResponse.getTeamMemberId();
            LocalDate pulseSubDate = pulseResponse.getSubmissionDate();
            LocalDate pulseUpDate = pulseResponse.getUpdatedDate();
            if(pulseResponse.getId()!=null){
            throw new PulseResponseBadArgException(String.format("Found unexpected id for pulseresponse %s", pulseResponse.getId()));
        } else if(!memberRepo.findById(memberId).isPresent()){
            throw new PulseResponseBadArgException(String.format("Member %s doesn't exists", memberId));
        } else if(pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
            throw new PulseResponseBadArgException(String.format("Invalid date for pulseresponse submission date %s",memberId));
        } else if(pulseUpDate.isBefore(LocalDate.EPOCH) || pulseUpDate.isAfter(LocalDate.MAX)) {
            throw new PulseResponseBadArgException(String.format("Invalid date for pulseresponse updated date %s",memberId));
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
            throw new PulseResponseBadArgException(String.format("Unable to find pulseresponse record with id %s", pulseResponse.getId()));
        }else if(!memberRepo.findById(memberId).isPresent()){
            throw new PulseResponseBadArgException(String.format("Member %s doesn't exist", memberId));
        } else if(memberId==null) {
            throw new PulseResponseBadArgException(String.format("Invalid pulseresponse %s", pulseResponse));
        } else if(pulseSubDate.isBefore(LocalDate.EPOCH) || pulseSubDate.isAfter(LocalDate.MAX)) {
            throw new PulseResponseBadArgException(String.format("Invalid date for pulseresponse submission date %s",memberId));
        } else if(pulseUpDate.isBefore(LocalDate.EPOCH) || pulseUpDate.isAfter(LocalDate.MAX)) {
            throw new PulseResponseBadArgException(String.format("Invalid date for pulseresponse %s",memberId));
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