package com.objectcomputing.checkins.gcp.chat;

import java.util.UUID;

public interface GoogleChatBotEntryRepo {

    GoogleChatBotEntry findByMemberId(UUID memberId);
}
