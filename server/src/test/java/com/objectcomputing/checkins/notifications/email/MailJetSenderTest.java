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
import jakarta.inject.Inject;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.*;

public class MailJetSenderTest extends TestContainersSuite {

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
