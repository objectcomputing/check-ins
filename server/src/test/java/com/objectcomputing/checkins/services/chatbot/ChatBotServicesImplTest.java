package com.objectcomputing.checkins.services.chatbot;

import com.google.auth.oauth2.GoogleCredentials;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBot;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBotEntry;
import com.objectcomputing.checkins.gcp.chat.GoogleChatBotEntryRepo;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.member_skill.MemberSkill;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;
import io.micronaut.security.authentication.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static com.objectcomputing.checkins.util.Util.nullSafeUUIDToString;
import static org.mockito.ArgumentMatchers.eq;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ChatBotServicesImplTest {

    @Mock
    private GoogleServiceConfiguration googleServiceConfiguration;

    @Mock
    private Authentication authentication;

    @Mock
    private GoogleApiAccess mockGoogleApiAccess;

    @Mock
    private GoogleChatBotEntryRepo chatBotEntryRepo;

    @Mock
    private MemberProfileFixture memberProfileFixture;

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
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(chatBotEntryRepo, memberProfileServices,
                credentials, currentUserServices, googleServiceConfiguration, authentication, mockGoogleApiAccess);
    }

    @Test
    void testSendNotification() {
        MemberProfile recipient = memberProfileFixture.createADefaultMemberProfile();
        String recipientId = nullSafeUUIDToString(recipient.getId());
        String notificationContent = "Sample";
        String spaceId = "";
        GoogleChatBotEntry entry = new GoogleChatBotEntry(spaceId, recipient.getId());
        doThrow().when(services).sendChat(notificationContent, recipientId);
//        verify(chatBotEntryRepo, times(1)).save(any(GoogleChatBotEntry.class));

    }
}
