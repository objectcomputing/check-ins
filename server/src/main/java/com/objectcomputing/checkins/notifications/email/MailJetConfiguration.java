package com.objectcomputing.checkins.notifications.email;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;

@Requires(property = MailJetConfiguration.PREFIX + ".from-address")
@Requires(property = MailJetConfiguration.PREFIX + ".from-name")
@ConfigurationProperties(MailJetConfiguration.PREFIX)
public interface MailJetConfiguration {

    String PREFIX = "mail-jet";

    String getFromAddress();

    String getFromName();
}
