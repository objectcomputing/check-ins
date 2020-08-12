package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.inject.Inject;

@MicronautTest(transactional = false)
@Testcontainers
public abstract class TestcontainerSuite implements RepositoryFixture {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11.6")
                .withDatabaseName("checkinsdb")
                .withUsername("postgres")
                .withPassword("postgres")
                .withReuse(true);

    @Inject
    private EmbeddedServer embeddedServer;

    @Override
    public EmbeddedServer getEmbeddedServer() {
        return embeddedServer;
    }
}
