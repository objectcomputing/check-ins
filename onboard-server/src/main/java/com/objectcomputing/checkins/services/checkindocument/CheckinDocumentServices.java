package com.objectcomputing.checkins.services.checkindocument;

import java.util.UUID;
import java.util.Set;

public interface CheckinDocumentServices {

    Set<CheckinDocument> read(UUID checkinsId);

    CheckinDocument getFindByUploadDocId(String uploadDocId);

    CheckinDocument save(CheckinDocument checkinDocument);

    CheckinDocument update(CheckinDocument checkinDocument);

    void deleteByCheckinId(UUID checkinsId);

    void deleteByUploadDocId(String uploadDocId);
}