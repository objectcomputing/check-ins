package com.objectcomputing.checkins.services.pulseresponse;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PulseResponseServicesImpl implements PulseResponseService {

    @Inject
    private PulseResponseRepository pulseResponseRepo;

    @Inject
    private MemberProfileRepository memberprofileRepo;

    public Set<PulseResponse> read(UUID teamMemberId) {

        Set<PulseResponse> pulseResponse = new HashSet<>();

        if (teamMemberId != null) {
            pulseResponse = pulseResponseRepo.findByTeamMemberId(teamMemberId);
        }
        return pulseResponse;
    }

    public PulseResponse save(PulseResponse pulseResponse) {

        PulseResponse newPulseResponse = null;

        if (pulseResponse != null) {
            if (pulseResponse.getTeamMemberId() == null || pulseResponse.getInternalFeelings() == null) {
                throw new PulseResponseBadArgException(String.format("Invalid PulseResponse %s", pulseResponse));
            } else if (pulseResponse.getTeamMemberId() == null || pulseResponse.getExternalFeelings() == null) {
                throw new PulseResponseBadArgException(String.format("Invalid PulseResponse %s", pulseResponse));
            } else if (pulseResponse.getId() != null) {
                throw new PulseResponseBadArgException(String.format("Found unexpected PulseResponse id %s, please try updating instead", pulseResponse.getId()));
            } else if (!memberprofileRepo.findById(pulseResponse.getTeamMemberId()).isPresent()) {
                throw new PulseResponseBadArgException(String.format("MemberProfile %s doesn't exist", pulseResponse.getTeamMemberId()));
            } else if (pulseResponseRepo.findByInternalFeelings(pulseResponse.getInternalFeelings()).isPresent()) {
                throw new PulseResponseBadArgException(String.format("PulseResponse with internalFeelings ID %s already exists", pulseResponse.getInternalFeelings()));
            } else if (pulseResponseRepo.findByExternalFeelings(pulseResponse.getExternalFeelings()).isPresent()) {
                throw new PulseResponseBadArgException(String.format("PulseResponse with externalFeelings ID %s already exists", pulseResponse.getExternalFeelings()));
            } else {
                newPulseResponse = pulseResponseRepo.save(pulseResponse);
            }
        }

        return newPulseResponse;
    }

    public PulseResponse update(PulseResponse pulseResponse) {

        PulseResponse updatedPulseResponse = null;

        if (pulseResponse != null) {
            if (pulseResponse.getTeamMemberId() == null || pulseResponse.getInternalFeelings() == null) {
                throw new PulseResponseBadArgException(String.format("Invalid PulseResponse %s", pulseResponse));
            }else if (pulseResponse.getTeamMemberId() == null || pulseResponse.getExternalFeelings() == null) {
                throw new PulseResponseBadArgException(String.format("Invalid PulseResponse %s", pulseResponse));
            } else if (pulseResponse.getId() == null || !pulseResponseRepo.findById(pulseResponse.getId()).isPresent()) {
                throw new PulseResponseBadArgException(String.format("PulseResponse id %s not found, please try inserting instead",
                        pulseResponse.getId()));
            } else if (!memberprofileRepo.findById(pulseResponse.getTeamMemberId()).isPresent()) {
                throw new PulseResponseBadArgException(String.format("MemberProfile %s doesn't exist", pulseResponse.getTeamMemberId()));
            } else {
                updatedPulseResponse = pulseResponseRepo.update(pulseResponse);
            }
        }

        return updatedPulseResponse;
    }

    public void delete(@NotNull UUID teamMemberId) {

        if(!pulseResponseRepo.existsByTeamMemberId(teamMemberId)) {
            throw new PulseResponseBadArgException(String.format("PulseResponse with TeamMemberId %s does not exist", teamMemberId));
        } else {
            pulseResponseRepo.deleteByTeamMemberId(teamMemberId);
        }
    }
}
