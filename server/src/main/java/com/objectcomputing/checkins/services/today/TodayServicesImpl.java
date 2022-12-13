package com.objectcomputing.checkins.services.today;

import com.objectcomputing.checkins.services.memberprofile.anniversaryreport.AnniversaryServices;
import com.objectcomputing.checkins.services.memberprofile.birthday.BirthDayServices;
import jakarta.inject.Singleton;

@Singleton
public class TodayServicesImpl implements TodayServices {

    private final BirthDayServices birthDayServices;
    private final AnniversaryServices anniversaryServices;

    public TodayServicesImpl(BirthDayServices birthDayServices, AnniversaryServices anniversaryServices) {
        this.birthDayServices = birthDayServices;
        this.anniversaryServices = anniversaryServices;
    }

    @Override
    public TodayResponseDTO getTodaysEvents() {
        TodayResponseDTO todaysEvents = new TodayResponseDTO();
        todaysEvents.setAnniversaries(anniversaryServices.getTodaysAnniversaries());
        todaysEvents.setBirthdays(birthDayServices.getTodaysBirthdays());
        return todaysEvents;
    }
}
