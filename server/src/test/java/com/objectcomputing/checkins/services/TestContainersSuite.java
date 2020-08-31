package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.annotation.MicronautTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import javax.inject.Inject;

@MicronautTest(environments = "test", transactional = false)
@Testcontainers
public abstract class TestContainersSuite implements RepositoryFixture {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11.6");

    @Inject
    private EmbeddedServer embeddedServer;

    @Inject
    private Flyway flyway;

    private final boolean shouldResetDBAfterEachTest;

    public TestContainersSuite() {
        this(true);
    }

    public TestContainersSuite(boolean shouldResetDBAfterEachTest) {
        this.shouldResetDBAfterEachTest = shouldResetDBAfterEachTest;
    }

    @BeforeEach
    private void setup() {
        flyway.migrate();
    }

    @AfterEach
    private void teardown() {
        if(shouldResetDBAfterEachTest) {
            flyway.clean();
        }
    }

    @Override
    public EmbeddedServer getEmbeddedServer() {
        return embeddedServer;
    }
}
