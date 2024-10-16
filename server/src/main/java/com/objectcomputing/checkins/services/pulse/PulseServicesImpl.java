package com.objectcomputing.checkins.services.pulse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.EmailSender;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.settings.SettingsServices;
import com.objectcomputing.checkins.services.settings.Setting;
import com.objectcomputing.checkins.services.email.AutomatedEmail;
import com.objectcomputing.checkins.services.email.AutomatedEmailRepository;
import com.objectcomputing.checkins.exceptions.NotFoundException;

import lombok.Getter;
import lombok.AllArgsConstructor;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
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
  private final AutomatedEmailRepository automatedEmailRepository;

  @Inject
  private PulseEmail email;

  private final DayOfWeek emailDay = DayOfWeek.MONDAY;

  private String setting = "bi-weekly";
  private final Map<String, Frequency> frequency = Map.of(
    "weekly", new Frequency(1, ChronoUnit.WEEKS),
    "bi-weekly", new Frequency(2, ChronoUnit.WEEKS),
    "monthly", new Frequency(1, ChronoUnit.MONTHS)
  );

  public PulseServicesImpl(
                    @Named(MailJetFactory.MJML_FORMAT) EmailSender emailSender,
                    CheckInsConfiguration checkInsConfiguration,
                    MemberProfileServices memberProfileServices,
                    SettingsServices settingsServices,
                    AutomatedEmailRepository automatedEmailRepository) {
    this.emailSender = emailSender;
    this.checkInsConfiguration = checkInsConfiguration;
    this.memberProfileServices = memberProfileServices;
    this.settingsServices = settingsServices;
    this.automatedEmailRepository = automatedEmailRepository;
  }

  public void sendPendingEmail(LocalDate check) {
    if (check.getDayOfWeek() == emailDay) {
      LOG.info("Checking for pending Pulse email");
      // Start from the first of the year and move forward to ensure that we
      // are sending email during the correct week.
      LocalDate start = check.with(TemporalAdjusters.firstDayOfYear())
                             .with(TemporalAdjusters.firstInMonth(emailDay));

      try {
        Setting freq = settingsServices.findByName("PULSE_EMAIL_FREQUENCY");
        if (frequency.containsKey(freq.getValue())) {
          setting = freq.getValue();
        } else {
          LOG.error("Invalid Pulse Email Frequency Setting: {}", freq);
        }
      } catch(NotFoundException ex) {
        // Use the default setting.
        LOG.error("Pulse Frequency Error", ex);
      }

      LOG.info("Using Pulse Frequency: {}", setting);
      final Frequency freq = frequency.get(setting);
      do {
        if (start.getDayOfMonth() == check.getDayOfMonth()) {
          LOG.info("Check day of month matches frequency day");
          final String key = new StringBuilder("pulse")
                             .append(start.getMonth().toString())
                             .append("_")
                             .append(String.valueOf(start.getDayOfMonth()))
                             .toString();
          Optional<AutomatedEmail> sent = automatedEmailRepository.findById(key);
          if (sent.isEmpty()) {
            LOG.info("Sending Pulse Email");
            email.send();
            automatedEmailRepository.save(new AutomatedEmail(key));
          } else {
            LOG.info("The Pulse Email has already been sent today");
          }
          break;
        }
        start = start.plus(freq.getCount(), freq.getUnits());

        // Apply firstInMonth(emailDay) to support adding one month to the start
        // date.  When adding weeks, it remains on the original day.  But, when
        // adding months, it can move away from the first of the month and we
        // need the day specified by emailDay.
        if (freq.getUnits() == ChronoUnit.MONTHS) {
          start = start.with(TemporalAdjusters.firstInMonth(emailDay));
        }
      } while(start.isBefore(check) || start.isEqual(check));
    }
  }
}
