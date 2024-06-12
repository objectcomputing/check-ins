package com.objectcomputing.checkins.notifications.email;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Property(name = "micronaut.http.client.read-timeout", value = "5h")
class MailJetSenderTest extends TestContainersSuite implements MemberProfileFixture {

    private static final String TEST_EMAIL_BODY = """
            {
                "subject": "Test Subject",
                "content": "Test Content",
                "recipients": ["tim@woo.com", "dave@whee.com"]
            }
            """;

    @Inject
    @Client("/services/email-notifications")
    HttpClient httpClient;

    @Inject
    @Named(MailJetConfig.HTML_FORMAT)
    EmailSender emailSender;

    @Test
    void testCreateBatchesForManyRecipients() {
        List<String> recipients = new ArrayList<>();
        int numRecipients = MailJetSender.MAILJET_RECIPIENT_LIMIT + 10;

        for (int i = 1; i <= numRecipients; i++) {
            recipients.add("recipient" + String.format("%02d", i) + "@objectcomputing.com");
        }

        List<JSONArray> batches = MailJetSender.getEmailBatches(recipients.toArray(String[]::new));

        assertEquals(2, batches.size());

        JSONArray firstBatch = batches.getFirst();
        assertEquals(MailJetSender.MAILJET_RECIPIENT_LIMIT, firstBatch.length());

        List<String> firstEmailGroup = recipients.subList(0, MailJetSender.MAILJET_RECIPIENT_LIMIT);
        for (int i = 0; i < MailJetSender.MAILJET_RECIPIENT_LIMIT; i++) {
            Object email = firstBatch.getJSONObject(i).get("Email");
            assertInstanceOf(String.class, email);
            assertTrue(firstEmailGroup.contains(email));
        }

        JSONArray secondBatch = batches.get(1);
        assertEquals(10, secondBatch.length());

        List<String> secondEmailGroup = recipients.subList(MailJetSender.MAILJET_RECIPIENT_LIMIT, numRecipients);
        for (int i = 0; i < 10; i++) {
            Object email = secondBatch.getJSONObject(i).get("Email");
            assertInstanceOf(String.class, email);
            assertTrue(secondEmailGroup.contains(email));
        }
    }

    @Test
    void successfulEndpointWorksAsExpected() {
        var tim = memberWithoutBoss("tim");

        when(emailSender.sendEmailReceivesStatus(
                tim.getFirstName() + " " + tim.getLastName(),
                tim.getWorkEmail(),
                "Test Subject",
                "Test Content",
                "tim@woo.com",
                "dave@whee.com"
        )).thenReturn(true);

        MutableHttpRequest<String> request = HttpRequest.POST("/", TEST_EMAIL_BODY).basicAuth(tim.getWorkEmail(), MEMBER_ROLE);
        assertEquals(HttpStatus.OK, httpClient.toBlocking().exchange(request).getStatus());
    }

    @Test
    void unsuccessfulEndpointWorksAsExpected() {
        var tim = memberWithoutBoss("tim");

        when(emailSender.sendEmailReceivesStatus(
                tim.getFirstName() + " " + tim.getLastName(),
                tim.getWorkEmail(),
                "Test Subject",
                "Test Content",
                "tim@woo.com",
                "dave@whee.com"
        )).thenReturn(false);

        MutableHttpRequest<String> request = HttpRequest.POST("/", TEST_EMAIL_BODY).basicAuth(tim.getWorkEmail(), MEMBER_ROLE);
        var err = assertThrows(HttpClientResponseException.class, () -> httpClient.toBlocking().exchange(request));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, err.getStatus());
    }

    @Replaces(MailJetSender.class)
    @MockBean(value = EmailSender.class, named = MailJetConfig.HTML_FORMAT)
    MailJetSender emailSender() {
        return mock(MailJetSender.class);
    }
}
