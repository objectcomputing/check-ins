package com.objectcomputing.checkins.services.pulse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import jakarta.inject.Singleton;
import jakarta.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.Readable;
import io.micronaut.core.io.IOUtils;

import java.io.BufferedReader;
import java.util.List;

@Singleton
class PulseEmail {
  private static final Logger LOG = LoggerFactory.getLogger(PulseEmail.class);
  private final EmailSender emailSender;
  private final CheckInsConfiguration checkInsConfiguration;
  private final MemberProfileServices memberProfileServices;

  private final String SUBJECT = "Check Out the Pulse Survey!";

  @Value("classpath:mjml/pulse_email.mjml")
  private Readable pulseEmailTemplate;
  
  public PulseEmail(@Named(MailJetFactory.MJML_FORMAT) EmailSender emailSender,
                    CheckInsConfiguration checkInsConfiguration,
                    MemberProfileServices memberProfileServices) {
    this.emailSender = emailSender;
    this.checkInsConfiguration = checkInsConfiguration;
    this.memberProfileServices = memberProfileServices;
  }

  private List<String> getActiveTeamMembers() {
    List<String> addresses = memberProfileServices.findAll().stream()
                                .filter(p -> p.getTerminationDate() == null)
                                .map(p -> p.getWorkEmail())
                                .toList();
    return addresses;
  }

  private String getEmailContent() {
    try {
      return String.format(IOUtils.readText(
                             new BufferedReader(pulseEmailTemplate.asReader())),
                           checkInsConfiguration.getWebAddress());
    } catch(Exception ex) {
      LOG.error(ex.toString());
      return "";
    }
  }

  public void send() {
    final List<String> recipients = getActiveTeamMembers();
    final String content = getEmailContent();
    emailSender.sendEmailBlind(null, null, SUBJECT, content,
                          recipients.toArray(new String[recipients.size()]));
  }
}
