package com.objectcomputing.checkins.notifications.email;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;

import com.mailjet.client.resource.Emailv31;
import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.settings.SettingOption;
import com.objectcomputing.checkins.services.settings.SettingsServices;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Factory
public class MailJetFactory {

    public static final String HTML_FORMAT = "html";
    public static final String TEXT_FORMAT = "text";

    @Inject
    SettingsServices settingsServices;

    @Bean
    MailjetClient getClient() {

        String mj_apikey_public;
        String mj_apikey_private;

        try {
            mj_apikey_public = settingsServices.findByName(SettingOption.MJ_APIKEY_PUBLIC.name()).getValue();
        } catch (NotFoundException e) {
            mj_apikey_public = "";
        }
        try {
            mj_apikey_private = settingsServices.findByName(SettingOption.MJ_APIKEY_PRIVATE.name()).getValue();
        } catch (NotFoundException e) {
            mj_apikey_private = "";
        }



        return new MailjetClient(
                ClientOptions
                        .builder()
                        .apiKey(mj_apikey_public)
                        .apiSecretKey(mj_apikey_private)
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
    @Named(TEXT_FORMAT)
    EmailSender getTextSender(MailJetSender sender) {
        sender.setEmailFormat(Emailv31.Message.TEXTPART);
        return sender;
    }
}
