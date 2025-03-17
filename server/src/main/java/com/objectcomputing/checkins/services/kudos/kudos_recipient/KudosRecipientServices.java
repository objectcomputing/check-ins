package com.objectcomputing.checkins.services.kudos.kudos_recipient;

import java.util.List;
import java.util.UUID;

public interface KudosRecipientServices {

    KudosRecipient save(KudosRecipient kudosRecipient);

    List<KudosRecipient> getAllByKudosId(UUID kudosId);
}
