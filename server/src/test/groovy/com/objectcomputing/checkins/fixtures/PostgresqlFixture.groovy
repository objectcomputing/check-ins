package com.objectcomputing.checkins.fixtures

import com.objectcomputing.checkins.Postgresql

trait PostgresqlFixture {

    Map<String, Object> getPostgresqlConfiguration() {
        if (Postgresql.postgreSQLContainer == null || !Postgresql.postgreSQLContainer.isRunning()) {
            Postgresql.init()
        }
        [
            'datasources.default.url'     : Postgresql.postgreSQLContainer.getJdbcUrl(),
            'datasources.default.password': Postgresql.postgreSQLContainer.getPassword(),
            'datasources.default.username': Postgresql.postgreSQLContainer.getUsername(),
        ]
    }
}
