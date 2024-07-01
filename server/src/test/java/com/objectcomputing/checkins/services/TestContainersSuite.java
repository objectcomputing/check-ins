package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;
import jakarta.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(environments = {Environment.TEST}, transactional = false)
public abstract class TestContainersSuite implements RepositoryFixture, TestPropertyProvider {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:11.6");

    @Inject
    private EmbeddedServer embeddedServer;

    @Inject
    private Flyway flyway;

    public TestContainersSuite() {}

    private void deleteAllEntities() {
        // Note order can matter here.
        getVolunteeringEventRepository().deleteAll();
        getVolunteeringRelationshipRepository().deleteAll();
        getVolunteeringOrganizationRepository().deleteAll();
        getEarnedCertificationRepository().deleteAll();
        getCertificationRepository().deleteAll();
        getEntityTagRepository().deleteAll();
        getTagRepository().deleteAll();
        getPulseResponseRepository().deleteAll();
        getCheckInNoteRepository().deleteAll();
        getPrivateNoteRepository().deleteAll();
        getCheckInDocumentRepository().deleteAll();
        getActionItemRepository().deleteAll();
        getAgendaItemRepository().deleteAll();
        getQuestionRepository().deleteAll();
        getMemberHistoryRepository().deleteAll();
        getMemberSkillRepository().deleteAll();
        getSkillCategorySkillRepository().deleteAll();
        getSkillCategoryRepository().deleteAll();
        getSkillRepository().deleteAll();
        getQuestionCategoryRepository().deleteAll();
        getSurveyRepository().deleteAll();
        getEmployeeHoursRepository().deleteAll();
        getFeedbackAnswerRepository().deleteAll();
        getTemplateQuestionRepository().deleteAll();
        getSettingsRepository().deleteAll();
        getOpportunitiesRepository().deleteAll();
        getDemographicsRepository().deleteAll();
        getRolePermissionRepository().deleteAll();
        getEmailRepository().deleteAll();
        getGuildMemberRepository().deleteAll();
        getGuildMemberHistoryRepository().deleteAll();
        getGuildRepository().deleteAll();
        getTeamMemberRepository().deleteAll();
        getTeamRepository().deleteAll();
        getMemberRoleRepository().deleteAll();
        getRoleRepository().deleteAll();
        getCheckInRepository().deleteAll();
        getFeedbackRequestRepository().deleteAll();
        getFeedbackTemplateRepository().deleteAll();
        getMemberProfileRepository().deleteAll();
        getReviewPeriodRepository().deleteAll();
        getReviewAssignmentRepository().deleteAll();
    }

    @BeforeEach
    public void setup() {
        deleteAllEntities();
        flyway.migrate();
    }

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
        HashMap<String, String> properties = new HashMap<>();
        properties.put("datasources.default.url", getJdbcUrl());
        properties.put("datasources.default.username", getUsername());
        properties.put("datasources.default.password", getPassword());
        properties.put("datasources.default.dialect", "POSTGRES");
        properties.put("datasources.default.driverClassName", "org.postgresql.Driver");
        properties.put("flyway.datasources.default.clean-schema", "true"); // Needed to run Flyway.clean()
        properties.put("mail-jet.from-address", "someEmail@gmail.com");
        properties.put("mail-jet.from-name", "John Doe");
        return properties;
    }

    static String getJdbcUrl() {
        return postgres.getJdbcUrl();
    }

    static String getUsername() {
        return postgres.getUsername();
    }

    static String getPassword() {
        return postgres.getPassword();
    }

    @Override
    public EmbeddedServer getEmbeddedServer() {
        return embeddedServer;
    }
}