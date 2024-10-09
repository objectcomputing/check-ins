package com.objectcomputing.checkins.services.pulse;

import com.objectcomputing.checkins.configuration.CheckInsConfiguration;
import com.objectcomputing.checkins.notifications.email.MailJetFactory;
import com.objectcomputing.checkins.services.MailJetFactoryReplacement;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.settings.SettingsServices;
import com.objectcomputing.checkins.services.settings.Setting;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Property;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.mailjet.factory", value = StringUtils.TRUE)
class PulseServicesTest extends TestContainersSuite implements TeamFixture, RoleFixture {
    @Inject
    @Named(MailJetFactory.MJML_FORMAT)
    private MailJetFactoryReplacement.MockEmailSender emailSender;

    @Inject
    CheckInsConfiguration checkInsConfiguration;

    @Inject
    MemberProfileServices memberProfileServices;

    @Inject
    SettingsServices settingsServices;

    @Inject
    PulseServices pulseServices;

    private MemberProfile member;
    private MemberProfile other;
    private MemberProfile admin;
    private String recipients;

    private LocalDate weeklyDate;
    private LocalDate biWeeklyDate;
    private LocalDate monthlyDate;

    private final String pulseSettingName = "PULSE_EMAIL_FREQUENCY";
    private final String pulseWeekly = "weekly";
    private final String pulseBiWeekly = "bi-weekly";
    private final String pulseMonthly = "monthly";

    @BeforeEach
    void setUp() {
        createAndAssignRoles();

        member = createADefaultMemberProfile();
        other = createASecondDefaultMemberProfile();

        admin = createAThirdDefaultMemberProfile();
        assignAdminRole(admin);

        List<String> recipientsEmail = List.of(member.getWorkEmail(),
                                               other.getWorkEmail(),
                                               admin.getWorkEmail());
        recipients = String.join(",", recipientsEmail);
        emailSender.reset();

        final LocalDate start =
          LocalDate.now()
                   .with(TemporalAdjusters.firstDayOfYear())
                   .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        weeklyDate = start.plus(23, ChronoUnit.WEEKS);
        biWeeklyDate = start.plus(42, ChronoUnit.WEEKS);
        monthlyDate = start.plus(2, ChronoUnit.MONTHS)
                        .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));
    }

    @Test
    void testBiWeeklySendEmail() {
        final Setting setting = new Setting(pulseSettingName, pulseBiWeekly);
        settingsServices.save(setting);

        pulseServices.sendPendingEmail(biWeeklyDate);
        assertEquals(1, emailSender.events.size());

        validateEmail("SEND_EMAIL", "null", "null",
                      "Check Out the Pulse Survey!",
                      "Please fill out your Pulse survey, if you haven't already done so.",
                      recipients,
                      emailSender.events.getFirst());
    }

    @Test
    void testWeeklySendEmail() {
        final Setting setting = new Setting(pulseSettingName, pulseWeekly);
        settingsServices.save(setting);

        pulseServices.sendPendingEmail(weeklyDate);
        assertEquals(1, emailSender.events.size());

        validateEmail("SEND_EMAIL", "null", "null",
                      "Check Out the Pulse Survey!",
                      "Please fill out your Pulse survey, if you haven't already done so.",
                      recipients,
                      emailSender.events.getFirst());
    }

    @Test
    void testMonthlySendEmail() {
        final Setting setting = new Setting(pulseSettingName, pulseMonthly);
        settingsServices.save(setting);

        pulseServices.sendPendingEmail(monthlyDate);
        assertEquals(1, emailSender.events.size());

        validateEmail("SEND_EMAIL", "null", "null",
                      "Check Out the Pulse Survey!",
                      "Please fill out your Pulse survey, if you haven't already done so.",
                      recipients,
                      emailSender.events.getFirst());
    }

    @Test
    void testDuplicateSendEmail() {
        final Setting setting = new Setting(pulseSettingName, pulseMonthly);
        settingsServices.save(setting);

        pulseServices.sendPendingEmail(monthlyDate);
        // This should be zero because email was already sent on this date.
        assertEquals(0, emailSender.events.size());
    }

    @Test
    void testNoSendEmail() {
        final Setting setting = new Setting(pulseSettingName, pulseBiWeekly);
        settingsServices.save(setting);

        pulseServices.sendPendingEmail(weeklyDate);
        // This should be zero because, when set to bi-weekly, email is sent on
        // the first, third, and fifth Monday of the month.
        assertEquals(0, emailSender.events.size());

        final LocalDate nonMonday = weeklyDate.plus(1, ChronoUnit.DAYS);
        pulseServices.sendPendingEmail(nonMonday);
        // This should be zero because the date is not a Monday.
        assertEquals(0, emailSender.events.size());
    }

    private void validateEmail(String action, String fromName,
                               String fromAddress, String subject,
                               String partial, String recipients,
                               List<String> event) {
        assertEquals(action, event.get(0));
        assertEquals(fromName, event.get(1));
        assertEquals(fromAddress, event.get(2));
        assertEquals(subject, event.get(3));
        if (partial != null && !partial.isEmpty()) {
            assertTrue(event.get(4).contains(partial));
        }
        assertEquals(recipients, event.get(5));
    }
}
