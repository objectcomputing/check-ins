package com.objectcomputing.checkings.gcp.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.chat.v1.HangoutsChat;
import com.google.api.services.chat.v1.model.Message;
import com.google.api.services.chat.v1.model.Thread;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.pubsub.v1.PubsubMessage;
import io.micronaut.gcp.pubsub.annotation.PubSubListener;
import io.micronaut.gcp.pubsub.annotation.Subscription;


@PubSubListener
public class GoogleChatBot {

    static final String BOT_NAME = "Check-Ins";
    static final String CHAT_BOT_SCOPE = "https://www.googleapis.com/auth/chat.bot";
    private final GoogleCredentials credentials;

    public GoogleChatBot(GoogleCredentials credentials) {
        this.credentials = credentials;
    }

    @Subscription("projects/oci-intern-2019/subscriptions/checkins-chat-bot")
    public void onMessage(PubsubMessage pubsubMessage) {
        try {
            String content = pubsubMessage.getData().toStringUtf8();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode chatEvent = mapper.readTree(content);

            Message reply = new Message();

            String eventType = chatEvent.path("type").asText();
            System.out.println(String.format("eventType = %s", eventType));
            switch (eventType) {
                case "ADDED_TO_SPACE":
                    // A bot can also be added to a room by @mentioning it in a message. In that case, we fall
                    // through to the MESSAGE case and let the bot respond. If the bot was added using the
                    // invite flow, we just post a thank you message in the space.
                    if (!chatEvent.has("message")) {
                        reply.setText("Thank you for adding me!");
                        break;
                    }
                case "MESSAGE":
                    String userText = chatEvent.at("/message/text").asText();
                    String threadName = chatEvent.at("/message/thread/name").asText();
                    reply.setText(String.format("You said: %s", userText))
                            .setThread(new Thread().setName(threadName));
                    System.out.println(String.format("You said: %s", userText));
                    break;
                default:
                    // Nothing to reply with, just ack the message and stop
                    return;
            }

            // Send the reply message via chat API.
            String spaceName = chatEvent.at("/space/name").asText();
            System.out.println(String.format("spaceName = %s", spaceName));
            GoogleCredentials credentials = this.credentials.createScoped(
                    CHAT_BOT_SCOPE
            );
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
            HangoutsChat chatService = new HangoutsChat.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    requestInitializer)
                    .setApplicationName(BOT_NAME)
                    .build();

            chatService.spaces().messages().create(spaceName, reply).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}