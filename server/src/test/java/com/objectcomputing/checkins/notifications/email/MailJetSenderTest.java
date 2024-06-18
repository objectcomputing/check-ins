package com.objectcomputing.checkins.notifications.email;

import com.objectcomputing.checkins.services.TestContainersSuite;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MailJetSenderTest extends TestContainersSuite {

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
}
