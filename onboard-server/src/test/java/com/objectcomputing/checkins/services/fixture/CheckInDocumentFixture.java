package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.checkindocument.CheckinDocument;
import com.objectcomputing.checkins.services.checkins.CheckIn;

public interface CheckInDocumentFixture extends RepositoryFixture {

    default CheckinDocument createADefaultCheckInDocument(CheckIn checkIn) {
        return getCheckInDocumentRepository().save(new CheckinDocument(checkIn.getId(), "doc1"));
    }

    default CheckinDocument createACustomCheckInDocument(CheckIn checkIn, String uploadDocId) {
        return getCheckInDocumentRepository().save(new CheckinDocument(checkIn.getId(), uploadDocId));
    }
}
