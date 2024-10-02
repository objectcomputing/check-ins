package com.objectcomputing.checkins.services.pulse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import jakarta.inject.Named;

import java.util.List;

class PulseEmail {
  private final EmailSender emailSender;
  private final CheckInsConfiguration checkInsConfiguration;
  private final MemberProfileServices memberProfileServices;

  private final String SUBJECT = "Check Out the Pulse Survey!";
  
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
    return String.format("""
<mjml>
  <mj-body>
    <mj-section>
      <mj-column>
        <mj-divider border-color="#2559a7"></mj-divider>
        <mj-text font-size="16px" font-family="'Helvetica Neue', Helvetica, Arial, sans-serif" color="#4d4c4f">Please fill out your Pulse survey, if you haven't already done so. We want to know how you're doing!</mj-text>
        <mj-text font-size="16px" font-family="'Helvetica Neue', Helvetica, Arial, sans-serif" color="#4d4c4f">Click <a href="%s/pulse" target="_blank">here</a> to begin.</mj-text>
      </mj-column>
    </mj-section>
  </mj-body>
</mjml>
""", checkInsConfiguration.getWebAddress());
  }

  public void send() {
    final List<String> recipients = getActiveTeamMembers();
    final String content = getEmailContent();
    emailSender.sendEmail(null, null, SUBJECT, content,
                          recipients.toArray(new String[recipients.size()]));
  }
}
