package com.objectcomputing.checkins.services.pulse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

@Singleton
public class PulseServicesImpl implements PulseServices {
  private static final Logger LOG = LoggerFactory.getLogger(PulseServicesImpl.class);
  private final EmailSender emailSender;
  private final CheckInsConfiguration checkInsConfiguration;
  private final MemberProfileServices memberProfileServices;
  private final Map<String, Boolean> sent = new HashMap<String, Boolean>();

  public PulseServicesImpl(
                    @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender,
                    CheckInsConfiguration checkInsConfiguration,
                    MemberProfileServices memberProfileServices) {
    this.emailSender = emailSender;
    this.checkInsConfiguration = checkInsConfiguration;
    this.memberProfileServices = memberProfileServices;

  }

  public void sendPendingEmail(LocalDate check) {
    if (check.getDayOfWeek() == DayOfWeek.MONDAY) {
      LOG.info("Checking for pending Pulse email");
      LocalDate now = LocalDate.now();
      LocalDate start = now.with(TemporalAdjusters.firstInMonth(
                                                         DayOfWeek.MONDAY));
      do {
        if (start.getDayOfMonth() == check.getDayOfMonth()) {
          LOG.info("Check day of month matches interval day");
          String key = new StringBuilder(start.getMonth().toString())
                             .append("_")
                             .append(String.valueOf(start.getDayOfMonth()))
                             .toString();
          if (!sent.containsKey(key)) {
            LOG.info("Sending Pulse Email");
            send();
            sent.put(key, true);
          }
          break;
        }
        start = start.plus(2, ChronoUnit.WEEKS);
      } while(start.getMonth() == now.getMonth());
    }
  }

  private void send() {
    PulseEmail email = new PulseEmail(emailSender, checkInsConfiguration,
                                      memberProfileServices);
    email.send();
  }
}
