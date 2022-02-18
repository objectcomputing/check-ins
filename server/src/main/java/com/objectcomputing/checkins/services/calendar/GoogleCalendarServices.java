package com.objectcomputing.checkins.services.calendar;

import com.google.api.services.calendar.model.Event;

import java.io.IOException;

public interface GoogleCalendarServices {
    String save() throws IOException;
    
}
