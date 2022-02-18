package com.objectcomputing.checkins.services.calendar;

import com.google.api.services.calendar.model.Event;
import io.micronaut.security.authentication.Authentication;

import java.io.IOException;

public interface GoogleCalendarServices {
    String save(Authentication authentication) throws IOException;
    
}
