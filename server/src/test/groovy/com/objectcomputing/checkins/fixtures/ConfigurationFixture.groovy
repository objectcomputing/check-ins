package com.objectcomputing.checkins.fixtures

trait ConfigurationFixture implements PostgresqlFixture {

    abstract int getCheckInServerPort()

    Map<String, Object> getConfiguration() {
        Map<String, Object> m = [:]

        if (specName) {
            m['spec.name'] = specName
        }

        if (checkInSpecName) {
            m['checkIns.base-url'] = "http://localhost:$checkInServerPort"
        }

        m += postgresqlConfiguration

        m
    }

    String getSpecName() {
        null
    }

    String getCheckInSpecName() {
        null
    }

}
