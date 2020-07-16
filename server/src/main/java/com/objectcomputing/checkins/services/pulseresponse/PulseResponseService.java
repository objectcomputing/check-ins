package com.objectcomputing.checkins.services.pulseresponse;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.micronaut.http.HttpResponse;

public class PulseResponseService {

    @Inject
    PulseResponseRepository pulseResponseRepository;

    HttpResponse<List<PulseResponse>> findBy(LocalDate dateFrom, LocalDate dateTo, UUID teamMemberId) {
        if(teamMemberId != null) {
            return HttpResponse
                    .ok()
                    .body(pulseResponseRepository.findByTeamMemberId(teamMemberId));
        } else if(dateFrom != null && dateTo != null) {
            return HttpResponse
                    .ok()
                    .body(pulseResponseRepository.findBySubmissionDateBetween(dateFrom, dateTo));
        }

        return HttpResponse.badRequest();
    }

    HttpResponse<PulseResponse> save(PulseResponse pulseResponse) {
        PulseResponse newPulseResponse = pulseResponseRepository.save(pulseResponse);
        
        return HttpResponse
                .created(newPulseResponse)
                .headers(headers -> headers.location(location(newPulseResponse.getId())));
    }

    HttpResponse<PulseResponse> update(PulseResponse pulseResponse) {

        if(pulseResponse.getId() == null) {
            return HttpResponse.badRequest();
        } else if(!pulseResponseRepository.existsById(pulseResponse.getId())) {
            return HttpResponse.notFound();
        }

        PulseResponse updatedPulseResponse = pulseResponseRepository.update(pulseResponse);
        return HttpResponse
                .ok()
                .headers(headers -> headers.location(location(updatedPulseResponse.getId())))
                .body(updatedPulseResponse);        
    }

    URI location(UUID uuid) {
        return URI.create("/pulse-response/" + uuid);
    }
}