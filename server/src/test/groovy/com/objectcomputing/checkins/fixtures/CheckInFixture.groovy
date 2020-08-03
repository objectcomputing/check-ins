package com.objectcomputing.checkins.fixtures

import com.objectcomputing.checkins.services.checkins.CheckIn
import com.objectcomputing.checkins.services.checkins.CheckInRepository

import java.sql.Date

trait CheckInFixture {

    abstract CheckInRepository getCheckInRepository()
    private static Date testDate = new Date(System.currentTimeMillis())

    CheckIn saveCheckIn() {
        CheckIn checkIn = new CheckIn(UUID.randomUUID(), UUID.randomUUID(), testDate)
        checkInRepository.save(checkIn)

        checkIn
    }
}
