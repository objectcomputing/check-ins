package com.objectcomputing.checkins.notifications.email;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

public class MailJetSenderTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/email")
    private HttpClient client;

    private final EmailSender emailSender = mock(EmailSender.class);

    @BeforeEach
    void resetMocks() {
        Mockito.reset(emailSender);
    }

    @Test
    void testSaveEmail() {
        MemberProfile admin = createADefaultMemberProfile();
        createAndAssignAdminRole(admin);
        MemberProfile recipient = createASecondDefaultMemberProfile();

        Email email = new Email("Email Subject", "Email contents", admin.getId(), recipient.getId());
        Email savedEmail = getEmailRepository().save(email);

        assertNotNull(savedEmail);
        assertEquals(email, savedEmail);
    }

    @Test
    void testSendAndSaveEmail() {
        MemberProfile admin = createADefaultMemberProfile();
        createAndAssignAdminRole(admin);
        MemberProfile recipient1 = createASecondDefaultMemberProfile();
        MemberProfile recipient2 = createAThirdDefaultMemberProfile();

        Map<String, Object> email = new HashMap<>();
        email.put("subject", "Email Subject");
        email.put("content", "Email content");
        email.put("html", false);
        email.put("recipients", List.of(recipient1.getWorkEmail(), recipient2.getWorkEmail()));

        final HttpRequest<?> request = HttpRequest.POST("", email)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<Email>> response = client.toBlocking()
                .exchange(request, Argument.listOf(Email.class));

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(2, response.getBody().get().size());

        Email firstEmailRes = response.getBody().get().get(0);
        assertEquals(email.get("subject"), firstEmailRes.getSubject());
        assertEquals(email.get("content"), firstEmailRes.getContents());
        assertEquals(admin.getId(), firstEmailRes.getSentBy());
        assertEquals(recipient1.getId(), firstEmailRes.getRecipient());
        assertTrue(firstEmailRes.getTransmissionDate().isAfter(firstEmailRes.getSendDate()));

        Email secondEmailRes = response.getBody().get().get(1);
        assertEquals(email.get("subject"), secondEmailRes.getSubject());
        assertEquals(email.get("content"), secondEmailRes.getContents());
        assertEquals(admin.getId(), secondEmailRes.getSentBy());
        assertEquals(recipient2.getId(), secondEmailRes.getRecipient());
        assertTrue(secondEmailRes.getTransmissionDate().isAfter(secondEmailRes.getSendDate()));
    }

    @Test
    void testSendAndSaveEmailUnauthorized() {
        MemberProfile member = createAnUnrelatedUser();

        MemberProfile recipient1 = createASecondDefaultMemberProfile();
        MemberProfile recipient2 = createAThirdDefaultMemberProfile();

        Map<String, Object> email = new HashMap<>();
        email.put("subject", "Email Subject");
        email.put("content", "Email content");
        email.put("html", false);
        email.put("recipients", List.of(recipient1.getWorkEmail(), recipient2.getWorkEmail()));

        final HttpRequest<?> request = HttpRequest.POST("", email)
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Argument.listOf(Email.class)));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("You are not authorized to do this operation", responseException.getMessage());
    }

    @Test
    void testCreateBatchesForManyRecipients() {
        List<String> recipients = new ArrayList<>();
        int numRecipients = MailJetSender.MAILJET_RECIPIENT_LIMIT + 10;

        for (int i = 1; i <= numRecipients; i++) {
            recipients.add("recipient" + String.format("%02d", i) + "@objectcomputing.com");
        }

        List<JSONArray> batches = MailJetSender.getEmailBatches(recipients.toArray(String[]::new));

        assertEquals(2, batches.size());

        JSONArray firstBatch = batches.get(0);
        assertEquals(MailJetSender.MAILJET_RECIPIENT_LIMIT, firstBatch.length());

        List<String> firstEmailGroup = recipients.subList(0, MailJetSender.MAILJET_RECIPIENT_LIMIT);
        for (int i = 0; i < MailJetSender.MAILJET_RECIPIENT_LIMIT; i++) {
            Object email = firstBatch.getJSONObject(i).get("Email");
            assertTrue(email instanceof String);
            assertTrue(firstEmailGroup.contains(email));
        }

        JSONArray secondBatch = batches.get(1);
        assertEquals(10, secondBatch.length());

        List<String> secondEmailGroup = recipients.subList(MailJetSender.MAILJET_RECIPIENT_LIMIT, numRecipients);
        for (int i = 0; i < 10; i++) {
            Object email = secondBatch.getJSONObject(i).get("Email");
            assertTrue(email instanceof String);
            assertTrue(secondEmailGroup.contains(email));
        }
    }
}
