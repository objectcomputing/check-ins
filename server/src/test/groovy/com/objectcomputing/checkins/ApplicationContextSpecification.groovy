package com.objectcomputing.checkins

import com.objectcomputing.checkins.fixtures.ConfigurationFixture
import io.micronaut.context.ApplicationContext
import io.micronaut.core.io.socket.SocketUtils
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

abstract class ApplicationContextSpecification extends Specification implements ConfigurationFixture, LeakageDetector {

    @Shared
    int checkInServerPort = SocketUtils.findAvailableTcpPort()

    @AutoCleanup
    @Shared
    ApplicationContext applicationContext = ApplicationContext.run(configuration)

    def cleanup() {
        assert !hasLeakage()
    }

}
