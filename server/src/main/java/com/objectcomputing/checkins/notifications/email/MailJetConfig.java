package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class MailJetConfig {

    @Bean
    MailjetClient getClient() {
        return new MailjetClient(
                ClientOptions
                        .builder()
                        .apiKey(System.getenv("MJ_APIKEY_PUBLIC"))
                        .apiSecretKey(System.getenv("MJ_APIKEY_PRIVATE"))
                        .build()
        );
    }

    @Bean
    EmailSender getFactory(MailJetSender sender) {
        return sender;
    }

}
