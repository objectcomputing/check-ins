package com.objectcomputing.checkins.gcp.postgres;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.configuration.jdbc.hikari.DatasourceConfiguration;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;

@Singleton
@Requires(env = Environment.GOOGLE_COMPUTE)
@Requires(property = GoogleCloudDatabaseSetup.CLOUD_DB_CONNECTION_NAME)
@Requires(property = GoogleCloudDatabaseSetup.DATASOURCES_DEFAULT_USERNAME)
@Requires(property = GoogleCloudDatabaseSetup.DATASOURCES_DEFAULT_PASSWORD)
public class GoogleCloudDatabaseSetup implements BeanCreatedEventListener<DatasourceConfiguration> {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleCloudDatabaseSetup.class);

    public static final String CLOUD_DB_CONNECTION_NAME = "cloud.db.connection.name";
    public static final String DATASOURCES_DEFAULT_USERNAME = "datasources.default.username";
    public static final String DATASOURCES_DEFAULT_PASSWORD = "datasources.default.password";

    private static final String DB_NAME = System.getenv("DB_NAME");
    private final String cloudSqlInstanceName;

    public GoogleCloudDatabaseSetup(@Property(name = CLOUD_DB_CONNECTION_NAME) String cloudSqlInstanceName,
            @Property(name = DATASOURCES_DEFAULT_USERNAME) String defaultUsername,
            @Property(name = DATASOURCES_DEFAULT_PASSWORD) String defaultPassword) {
        this.cloudSqlInstanceName = cloudSqlInstanceName;
        LOG.warn("cloudSqlInstanceName = {}", cloudSqlInstanceName);
        LOG.warn("defaultUsername = {}", defaultUsername);
        LOG.warn("defaultPassword = {}", defaultPassword);
        LOG.warn("DB_NAME = {}", DB_NAME);
    }

    @Override
    public DatasourceConfiguration onCreated(BeanCreatedEvent<DatasourceConfiguration> event) {
        DatasourceConfiguration config = event.getBean();
        config.addDataSourceProperty("socketFactory", "com.google.cloud.sql.postgres.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance", cloudSqlInstanceName);
        if (DB_NAME != null) {
            config.setJdbcUrl(String.format("jdbc:postgresql:///%s", DB_NAME));
            LOG.warn("JDBC URL = {}", String.format("jdbc:postgresql:///%s", DB_NAME));
        }
        return config;
    }
}