package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.slack.SlackSignature;
import com.objectcomputing.checkins.services.slack.kudos.KudosChannelReader;
import com.objectcomputing.checkins.services.slack.kudos.AutomatedKudos;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.kudos.KudosResponseDTO;
import com.objectcomputing.checkins.services.role.RoleType;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.SlackSenderReplacement;
import com.objectcomputing.checkins.services.SlackReaderReplacement;
import com.objectcomputing.checkins.services.SlackSearchReplacement;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.AutomatedKudosFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;

import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.slacksearch", value = StringUtils.TRUE)
@Property(name = "replace.slackreader", value = StringUtils.TRUE)
@Property(name = "replace.slacksender", value = StringUtils.TRUE)
class SlackKudosTest extends TestContainersSuite implements MemberProfileFixture, AutomatedKudosFixture, RoleFixture {
    @Inject
    private SlackReaderReplacement slackReader;

    @Inject
    private SlackSearchReplacement slackSearch;

    @Inject
    private SlackSenderReplacement slackSender;

    @Inject
    private KudosChannelReader kudosChannelReader;

    @Inject
    private SlackSignature slackSignature;

    @Inject
    @Client("/services")
    protected HttpClient client;

    MemberProfile sender;
    MemberProfile recipient;

    @BeforeEach
    void setUp() {
        createAndAssignRoles();
        sender = createADefaultMemberProfile();
        recipient = createASecondDefaultMemberProfile();
    }

    @Test
    void testCreateKudosFromSlackMessage() throws JsonProcessingException {
        String slackSenderId = "senderId";
        slackSearch.users.put(slackSenderId, sender.getWorkEmail());
        String slackRecipient = "recipientId";
        slackSearch.users.put(slackRecipient, recipient.getWorkEmail());

        // Post to "Slack"
        final String beginning = "Kudos to ";
        slackReader.addMessage("SLACK_KUDOS_CHANNEL_ID", slackSenderId,
                               beginning + "<@" + slackRecipient + ">",
                               LocalDateTime.now());

        final String messageWithName = beginning +
                               MemberProfileUtils.getFullName(recipient);

        // Manually tell the reader to load messages from slack.  This normally
        // happens on an interval.
        kudosChannelReader.readChannel();

        // Get the automated kudos from the repository and validate.
        List<AutomatedKudos> generatedKudos = getAutomatedKudos();
        assertEquals(1, generatedKudos.size());
        AutomatedKudos kudos = generatedKudos.get(0);
        assertEquals(messageWithName, kudos.getMessage());
        assertEquals(slackSenderId, kudos.getExternalId());
        assertEquals(sender.getId(), kudos.getSenderId());
        assertEquals(1, kudos.getRecipientIds().size());
        assertEquals(recipient.getId().toString(),
                     kudos.getRecipientIds().get(0));

        // A slack message should have been sent to the sender as well...
        assertTrue(slackSender.sent.containsKey(slackSenderId));
        List<String> messages = slackSender.sent.get(slackSenderId);
        assertEquals(1, messages.size());
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode sent = mapper.readTree(messages.get(0));
        assertEquals(JsonNodeType.ARRAY, sent.getNodeType());

        var iter = sent.elements();
        assertTrue(iter.hasNext());
        JsonNode section = iter.next();
        
        assertEquals(JsonNodeType.OBJECT, section.getNodeType());
        assertTrue(section.has("block_id"));
        UUID automatedKudosUUID =
                UUID.fromString(section.get("block_id").asText());

        // Post to /external to say "yes, we want it in Check-Ins".
        final String rawBody = getSlackPostPayload(
                                   automatedKudosUUID.toString());
        long currentTime = Instant.now().getEpochSecond();
        String timestamp = String.valueOf(currentTime);

        HttpRequest request =
            HttpRequest.POST("/pulse-responses/external", rawBody)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("X-Slack-Signature", slackSignature.generate(timestamp, rawBody))
        .header("X-Slack-Request-Timestamp", timestamp);

        HttpResponse response = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, response.getStatus());

        // Get list of kudos from kudos controller and verify.
        request = HttpRequest.GET("/kudos/recent")
                             .basicAuth(sender.getWorkEmail(),
                                        RoleType.Constants.MEMBER_ROLE);
        HttpResponse<List<KudosResponseDTO>> list =
            client.toBlocking()
                  .exchange(request, Argument.listOf(KudosResponseDTO.class));
        assertEquals(HttpStatus.OK, list.getStatus());
        assertEquals(1, list.body().size());
        KudosResponseDTO element = list.body().getFirst();
        assertEquals(messageWithName, element.getMessage());
        assertEquals(sender.getId(), element.getSenderId());
        assertTrue(element.getPubliclyVisible());
        assertEquals(element.getRecipientMembers(), List.of(recipient));
    }

    @Test
    void testNoSlackMessages() {
        kudosChannelReader.readChannel();
        List<AutomatedKudos> generatedKudos = getAutomatedKudos();
        assertEquals(0, generatedKudos.size());
    }

    String getSlackPostPayload(String kudosId) {
        return "payload=" +
               URLEncoder.encode(String.format("""
{
  "type": "block_actions",
  "message": {
    "blocks": [
      {
        "block_id": "%s"
      }
    ]
  },
  "actions": [
    {
      "action_id": "yes_button"
    }
  ]
}
""", kudosId));
    }
}
