package com.objectcomputing.checkins

import org.testcontainers.containers.PostgreSQLContainer

class Postgresql {

    static PostgreSQLContainer postgreSQLContainer

    static init() {
        if (postgreSQLContainer == null) {
            postgreSQLContainer = new PostgreSQLContainer('postgres:11.5-alpine')

            // Reuse container between tests instead of starting a new one per execution
            postgreSQLContainer
                .withReuse(true)
                .withNetwork(null)
                .withLabel("com.objectcomputing.checkins", "postgresqlTescontainersReuse")
                .start()
        }
    }
}
