package com.objectcomputing.checkins.fixtures

import com.objectcomputing.checkins.services.checkins.CheckIn
import com.objectcomputing.checkins.services.checkins.CheckInRepository
import java.time.LocalDate

trait CheckInFixture {

    abstract CheckInRepository getCheckInRepository()
    private static LocalDate testDate = LocalDate.now()

    CheckIn saveCheckIn() {
        CheckIn checkIn = new CheckIn(UUID.randomUUID(), UUID.randomUUID(), testDate)
        checkInRepository.save(checkIn)

        checkIn
    }
}
