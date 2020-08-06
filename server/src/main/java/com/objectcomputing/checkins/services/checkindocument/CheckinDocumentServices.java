package com.objectcomputing.checkins.services.checkindocument;

import java.util.UUID;
import java.util.Set;

public interface CheckinDocumentServices {

    Set<CheckinDocument> read(UUID checkinsId);

    CheckinDocument save(CheckinDocument c);

    CheckinDocument update(CheckinDocument c);

    void delete(UUID checkinsId);
}