package com.objectcomputing.checkins.services.calendar;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Singleton;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.objectcomputing.checkins.security.GoogleServiceConfiguration;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.currentuser.CurrentUserServices;
import com.objectcomputing.checkins.util.googleapiaccess.GoogleApiAccess;

import com.objectcomputing.checkins.exceptions.BadArgException;
@Singleton
public class GoogleCalendarServicesImpl implements GoogleCalendarServices {

    private final GoogleApiAccess googleApiAccess;
    private final MemberProfileServices memberProfileServices;
    private final CurrentUserServices currentUserServices;
    private final GoogleServiceConfiguration googleServiceConfiguration;

    // Refer to the Java quickstart on how to setup the environment:
    // https://developers.google.com/calendar/quickstart/java
    // Change the scope to CalendarScopes.CALENDAR and delete any stored
    // credentials.
    public GoogleCalendarServicesImpl(GoogleApiAccess googleApiAccess,
            MemberProfileServices memberProfileServices,
            CurrentUserServices currentUserServices,
            GoogleServiceConfiguration googleServiceConfiguration) {
        this.googleApiAccess = googleApiAccess;
        this.memberProfileServices = memberProfileServices;
        this.currentUserServices = currentUserServices;
        this.googleServiceConfiguration = googleServiceConfiguration;
    }
    private void validate(boolean isError, String message, Object... args) {
        if(isError) {
            throw new BadArgException(String.format(message, args));
        }
    }

    @Override
    public String save() {
        Calendar calendarService = googleApiAccess.getCalendar();
        validate(calendarService == null, "Unable to access Google Calendars");
        Event event = new Event()
                .setSummary("Check-Ins 2022")
                .setLocation("800 Howard St., San Francisco, CA 94103")
                .setDescription("Testing Google Calendar integration.");

        DateTime startDateTime = new DateTime("2022-02-01T09:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2022-02-01T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        String[] recurrence = new String[] { "RRULE:FREQ=DAILY;COUNT=2" };
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[] {
                new EventAttendee().setEmail(currentUserServices.getCurrentUser().getWorkEmail()),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        try {
            event = calendarService.events().insert(calendarId, event).execute();
            return event.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return e.toString();
        }
    }
}
