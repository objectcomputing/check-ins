package com.objectcomputing.checkins.gcp.chat;

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
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import io.micronaut.gcp.pubsub.annotation.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micronaut.gcp.pubsub.annotation.PubSubListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@PubSubListener
public class GoogleChatBot {

    static final String BOT_NAME = "Check-Ins";
    static final String CHAT_BOT_SCOPE = "https://www.googleapis.com/auth/chat.bot";
    private GoogleCredentials credentials;
    private final MemberProfileServices memberProfileServices;
    private final GoogleChatBotEntryRepo chatBotRepo;
    private final CurrentUserServices currentUserServices;
    private static final Logger LOG = LoggerFactory.getLogger(GoogleChatBot.class);

    public GoogleChatBot(GoogleCredentials credentials, MemberProfileServices memberProfileServices, GoogleChatBotEntryRepo chatBotRepo, CurrentUserServices currentUserServices) {
        this.credentials = credentials;
        this.memberProfileServices = memberProfileServices;
        this.chatBotRepo = chatBotRepo;
        this.currentUserServices = currentUserServices;
    }

    public void setCredentials(GoogleCredentials credentials) {
        this.credentials = credentials;
    }

    public void sendChat(String content, String ...recipients) {
        try {
            List<GoogleChatBotEntry> toBeSent = new ArrayList<>();
                for (String recipient : recipients) {
                    Set<MemberProfile> memberProfile = memberProfileServices.findByValues(null, null, null, null, recipient, null, null);
                    if (memberProfile.size() == 1) {
                        MemberProfile[] entryArr = memberProfile.toArray(new MemberProfile[memberProfile.size()]);
                        //use google chat bot repo find by member id to see whcih reicpients have activated-->get space ID
                        GoogleChatBotEntry entry = chatBotRepo.findByMemberId(entryArr[0].getId());
                        if (entry != null) {
                            toBeSent.add(entry);
                        }

                    }

                }

                if (toBeSent.size() == 0) {
                    return;
                }

                for (GoogleChatBotEntry entry : toBeSent) {
                    Message reminder = new Message();
                    reminder.setText(content);
                    // Send the reply message via chat API.
                    String spaceName = entry.getSpaceId();
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

                    chatService.spaces().messages().create(spaceName, reminder).execute();
                }

            } catch(Exception e ){
                e.printStackTrace();
            }

        }



    @Subscription("projects/oci-intern-2019/subscriptions/checkins-chat-bot")
    public void onMessage(PubsubMessage pubsubMessage) {
        try {
            String content = pubsubMessage.getData().toStringUtf8();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode chatEvent = mapper.readTree(content);
            String senderEmail = chatEvent.findValue("email").toString();
            senderEmail = senderEmail.substring(1, senderEmail.length()-1);
            Message reply = new Message();

            String eventType = chatEvent.path("type").asText();
            switch (eventType) {
                case "ADDED_TO_SPACE":
                    // A bot can also be added to a room by @mentioning it in a message. In that case, we fall
                    // through to the MESSAGE case and let the bot respond. If the bot was added using the
                    // invite flow, we just post a thank you message in the space.
                    if (!chatEvent.has("message")) {
                        reply.setText("Thank you for adding me!");
                        Set<MemberProfile> memberProfile = memberProfileServices.findByValues(null, null, null, null, senderEmail, null, null);
                        if (memberProfile.size() == 1) {
                            MemberProfile[] entryArr = memberProfile.toArray(new MemberProfile[memberProfile.size()]);
                            GoogleChatBotEntry entry = new GoogleChatBotEntry(chatEvent.at("/space/name").asText(), entryArr[0].getId());
                            GoogleChatBotEntry saved = chatBotRepo.save(entry);
                        }
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