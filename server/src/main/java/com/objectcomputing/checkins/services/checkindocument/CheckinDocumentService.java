package com.objectcomputing.checkins.services.checkindocument;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import io.micronaut.http.HttpResponse;

public class CheckinDocumentService {

    @Inject
    CheckinDocumentRepository checkinDocumentRepository;

    URI location(UUID uuid) {
        return URI.create("/services/checkin-document" + uuid);
    }
}