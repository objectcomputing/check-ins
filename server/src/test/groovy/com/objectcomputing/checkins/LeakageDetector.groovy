package com.objectcomputing.checkins

import com.objectcomputing.checkins.fixtures.RepositoriesFixture

trait LeakageDetector extends RepositoriesFixture {

    boolean hasLeakage() {
        (
            memberProfileRepository.count() > 0 ||
            checkInRepository.count() > 0 ||
            skillRepository.count() > 0
        )
    }
}
