package com.objectcomputing.checkins.services.pulse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.settings.SettingsServices;
import com.objectcomputing.checkins.services.settings.Setting;
import com.objectcomputing.checkins.exceptions.NotFoundException;

import lombok.Getter;
import lombok.AllArgsConstructor;

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
  @Getter
  @AllArgsConstructor
  private class Frequency {
    private final int count;
    private final ChronoUnit units;
  }

  private static final Logger LOG = LoggerFactory.getLogger(PulseServicesImpl.class);
  private final EmailSender emailSender;
  private final CheckInsConfiguration checkInsConfiguration;
  private final MemberProfileServices memberProfileServices;
  private final SettingsServices settingsServices;
  private final Map<String, Boolean> sent = new HashMap<String, Boolean>();

  private String setting = "bi-weekly";
  private final Map<String, Frequency> frequency = Map.of(
    "weekly", new Frequency(1, ChronoUnit.WEEKS),
    "bi-weekly", new Frequency(2, ChronoUnit.WEEKS),
    "monthly", new Frequency(1, ChronoUnit.MONTHS)
  );

  public PulseServicesImpl(
                    @Named(MailJetFactory.HTML_FORMAT) EmailSender emailSender,
                    CheckInsConfiguration checkInsConfiguration,
                    MemberProfileServices memberProfileServices,
                    SettingsServices settingsServices) {
    this.emailSender = emailSender;
    this.checkInsConfiguration = checkInsConfiguration;
    this.memberProfileServices = memberProfileServices;
    this.settingsServices = settingsServices;
  }

  public void sendPendingEmail(LocalDate check) {
    if (check.getDayOfWeek() == DayOfWeek.THURSDAY) {
      LOG.info("Checking for pending Pulse email");
      final LocalDate now = LocalDate.now();
      LocalDate start = now.with(
                          TemporalAdjusters.firstInMonth(DayOfWeek.THURSDAY));

      try {
        Setting freq = settingsServices.findByName("PULSE_EMAIL_FREQUENCY");
        if (frequency.containsKey(freq.getValue())) {
          setting = freq.getValue();
        } else {
          LOG.error("Invalid Pulse Email Frequency Setting: " + freq.getValue());
        }
      } catch(NotFoundException ex) {
        // Use the default setting.
        LOG.error("Pulse Frequency Error: " + ex.toString());
      }

      LOG.info("Using Pulse Frequency: " + setting);
      final Frequency freq = frequency.get(setting);
      do {
        if (start.getDayOfMonth() == check.getDayOfMonth()) {
          LOG.info("Check day of month matches frequency day");
          final String key = new StringBuilder(start.getMonth().toString())
                             .append("_")
                             .append(String.valueOf(start.getDayOfMonth()))
                             .toString();
          if (sent.containsKey(key)) {
            LOG.info("The Pulse Email has already been sent today");
          } else {
            LOG.info("Sending Pulse Email");
            send();
            sent.put(key, true);
          }
          break;
        }
        start = start.plus(freq.getCount(), freq.getUnits());
      } while(start.getMonth() == now.getMonth());
    }
  }

  private void send() {
    PulseEmail email = new PulseEmail(emailSender, checkInsConfiguration,
                                      memberProfileServices);
    email.send();
  }
}
