package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;

import com.mailjet.client.resource.Emailv31;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Factory
public class MailJetFactory {

    public static final String HTML_FORMAT = "html";
    public static final String MJML_FORMAT = "mjml";
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

    @Singleton
    @Named(HTML_FORMAT)
    EmailSender getHtmlSender(MailJetSender sender) {
        sender.setEmailFormat(Emailv31.Message.HTMLPART);
        return sender;
    }

    @Singleton
    @Named(MJML_FORMAT)
    EmailSender getMjmlSender(MailJetSender sender) {
        sender.setEmailFormat(MailJetSender.MJMLPART);
        return sender;
    }

    @Singleton
    @Named(TEXT_FORMAT)
    EmailSender getTextSender(MailJetSender sender) {
        sender.setEmailFormat(Emailv31.Message.TEXTPART);
        return sender;
    }
}
