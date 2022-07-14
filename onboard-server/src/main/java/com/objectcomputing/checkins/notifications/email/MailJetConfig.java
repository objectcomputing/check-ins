package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;

import com.mailjet.client.resource.Emailv31;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import jakarta.inject.Named;

@Factory
public class MailJetConfig {

    public static final String HTML_FORMAT = "html";
    public static final String TEXT_FORMAT = "text";

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
    @Named(HTML_FORMAT)
    EmailSender getHtmlSender(MailJetSender sender) {
        sender.setEmailFormat(Emailv31.Message.HTMLPART);
        return sender;
    }

    @Bean
    @Named(TEXT_FORMAT)
    EmailSender getTextSender(MailJetSender sender) {
        sender.setEmailFormat(Emailv31.Message.TEXTPART);
        return sender;
    }

}
