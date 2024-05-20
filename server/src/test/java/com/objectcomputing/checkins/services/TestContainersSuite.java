package com.objectcomputing.checkins.services;

import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Testcontainers;

@MicronautTest(environments = {Environment.TEST}, transactional = false)
@Testcontainers
public abstract class TestContainersSuite implements RepositoryFixture {

    @Inject
    private EmbeddedServer embeddedServer;

    @Inject
    private Flyway flyway;

    @Value("${aes.key}")
    protected String key;

    public TestContainersSuite() {}

    private void deleteAllEntities() {
        // Note order can matter here.
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
    public EmbeddedServer getEmbeddedServer() {
        return embeddedServer;
    }
}
