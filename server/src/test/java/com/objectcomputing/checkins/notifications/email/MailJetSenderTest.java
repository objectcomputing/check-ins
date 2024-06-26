package com.objectcomputing.checkins.notifications.email;

import com.objectcomputing.checkins.services.TestContainersSuite;
import io.micronaut.inject.qualifiers.Qualifiers;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MailJetSenderTest extends TestContainersSuite {

    @Test
    void namedSendersAreSingletons() {
        // given
        var htmlSender = getEmailSender(MailJetFactory.HTML_FORMAT);
        var textSender = getEmailSender(MailJetFactory.TEXT_FORMAT);
        var secondHtmlSender = getEmailSender(MailJetFactory.HTML_FORMAT);

        // then htmlSender and secondHtmlSender should be the same instance
        assertSame(htmlSender, secondHtmlSender);

        // then textSender should be a different instance to htmlSender
        assertNotSame(htmlSender, textSender);

        // then textSender should be a different instance to secondHtmlSender
        assertNotSame(secondHtmlSender, textSender);
    }

    private EmailSender getEmailSender(String name) {
        return getEmbeddedServer().getApplicationContext().getBean(EmailSender.class, Qualifiers.byName(name));
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
