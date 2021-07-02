package com.objectcomputing.checkings.gcp.chat;

import io.micronaut.gcp.pubsub.annotation.PubSubListener;
import io.micronaut.gcp.pubsub.annotation.Subscription;

@PubSubListener
public class GoogleChatBot {
    /**
     *
     * @param data raw data
     */
    @Subscription("projects/oci-intern-2019/subscriptions/checkins-chat-bot")
    public void onMessage(byte[] data) {
        System.out.println("Message received: "+ data);
    }
}