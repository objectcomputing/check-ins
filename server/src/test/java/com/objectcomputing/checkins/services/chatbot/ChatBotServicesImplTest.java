package com.objectcomputing.checkins.services.chatbot;

import com.google.auth.oauth2.GoogleCredentials;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBot;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBotEntryRepo;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentRepository;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentServicesImpl;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatBotServicesImplTest {
    @Mock
    private GoogleChatBotEntryRepo chatBotEntryRepo;

    @Mock
    private MemberProfileServices memberProfileServices;

    @Mock
    private CurrentUserServices currentUserServices;

    @Mock
    private GoogleCredentials credentials;

    @InjectMocks
    private GoogleChatBot services;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(chatBotEntryRepo, memberProfileServices, credentials, currentUserServices);
    }
}
