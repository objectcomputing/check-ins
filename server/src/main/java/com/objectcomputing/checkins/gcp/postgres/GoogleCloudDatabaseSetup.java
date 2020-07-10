package com.objectcomputing.checkins.gcp.postgres;

import javax.inject.Singleton;

import io.micronaut.configuration.jdbc.hikari.DatasourceConfiguration;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;

@Singleton
@Requires(env = Environment.GOOGLE_COMPUTE)
@Requires(property = GoogleCloudDatabaseSetup.CLOUD_SQL_CONNECTION_NAME)
public class GoogleCloudDatabaseSetup implements BeanCreatedEventListener<DatasourceConfiguration> {
    public static final String CLOUD_SQL_CONNECTION_NAME = "cloud.db.connection.name";
    private static final String DB_NAME = System.getenv("DB_NAME");
    private final String cloudSqlInstanceName;

    public GoogleCloudDatabaseSetup(@Property(name = CLOUD_SQL_CONNECTION_NAME) String cloudSqlInstanceName) {
        this.cloudSqlInstanceName = cloudSqlInstanceName;
    }

    @Override
    public DatasourceConfiguration onCreated(BeanCreatedEvent<DatasourceConfiguration> event) {
        DatasourceConfiguration config = event.getBean();
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", cloudSqlInstanceName);
        if (DB_NAME != null) {
            config.setJdbcUrl(String.format("jdbc:postgresql:///%s", DB_NAME));
        }
        return config;
    }
}