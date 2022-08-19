package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.agenda_item.AgendaItemRepository;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNoteRepository;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentRepository;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.demographics.DemographicsRepository;
import com.objectcomputing.checkins.services.email.EmailRepository;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursRepository;
import com.objectcomputing.checkins.services.employmentpreferences.EmploymentDesiredAvailabilityRepository;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerRepository;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateRepository;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionRepository;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberHistoryRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.onboardeeprofile.OnboardingProfileRepository;
import com.objectcomputing.checkins.services.opportunities.OpportunitiesRepository;
import com.objectcomputing.checkins.services.onboard.background_information.BackgroundInformationRepository;
import com.objectcomputing.checkins.services.onboard.onboardee_employment_eligibility.OnboardeeEmploymentEligibilityRepository;
import com.objectcomputing.checkins.services.permissions.PermissionRepository;
import com.objectcomputing.checkins.services.private_notes.PrivateNoteRepository;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseRepository;
import com.objectcomputing.checkins.services.question_category.QuestionCategoryRepository;
import com.objectcomputing.checkins.services.questions.QuestionRepository;
import com.objectcomputing.checkins.services.referraltype.ReferralTypeRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleRepository;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionRepository;
import com.objectcomputing.checkins.services.settings.SettingsRepository;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import com.objectcomputing.checkins.services.survey.SurveyRepository;
import com.objectcomputing.checkins.services.tags.TagRepository;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTagRepository;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.services.team.member.MemberHistoryRepository;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
import io.micronaut.runtime.server.EmbeddedServer;

public interface RepositoryFixture {
    EmbeddedServer getEmbeddedServer();

    default MemberHistoryRepository getMemberHistoryRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberHistoryRepository.class);
    }

    default TagRepository getTagRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(TagRepository.class);
    }

    default FeedbackTemplateRepository getFeedbackTemplateRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(FeedbackTemplateRepository.class);
    }

    default TemplateQuestionRepository getTemplateQuestionRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(TemplateQuestionRepository.class);
    }

    default EntityTagRepository getEntityTagRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(EntityTagRepository.class);
    }

    default MemberProfileRepository getMemberProfileRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberProfileRepository.class);
    }

    default RoleRepository getRoleRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(RoleRepository.class);
    }

    default PulseResponseRepository getPulseResponseRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(PulseResponseRepository.class);
    }
    default SkillRepository getSkillRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SkillRepository.class);
    }

    default CheckInRepository getCheckInRepository(){
        return getEmbeddedServer().getApplicationContext().getBean(CheckInRepository.class);
    }

    default CheckinNoteRepository getCheckInNoteRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(CheckinNoteRepository.class);
    }

    default PrivateNoteRepository getPrivateNoteRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(PrivateNoteRepository.class);
    }

    default CheckinDocumentRepository getCheckInDocumentRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(CheckinDocumentRepository.class);
    }

    default ActionItemRepository getActionItemRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(ActionItemRepository.class);
    }

    default AgendaItemRepository getAgendaItemRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(AgendaItemRepository.class);
    }

    default TeamRepository getTeamRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(TeamRepository.class);
    }

    default TeamMemberRepository getTeamMemberRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(TeamMemberRepository.class);
    }

    default QuestionRepository getQuestionRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(QuestionRepository.class);
    }

    default MemberSkillRepository getMemberSkillRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberSkillRepository.class);
    }

    default GuildRepository getGuildRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(GuildRepository.class);
    }

    default GuildMemberRepository getGuildMemberRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(GuildMemberRepository.class);
    }
    default FeedbackRequestRepository getFeedbackRequestRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(FeedbackRequestRepository.class);
    }

    default QuestionCategoryRepository getQuestionCategoryRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(QuestionCategoryRepository.class);
    }
        
    default SurveyRepository getSurveyRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SurveyRepository.class);
    }

    default EmployeeHoursRepository getEmployeeHoursRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(EmployeeHoursRepository.class);
    }

    default FeedbackAnswerRepository getFeedbackAnswerRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(FeedbackAnswerRepository.class);
    }

     default SettingsRepository getSettingsRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SettingsRepository.class);
    }

    default OpportunitiesRepository getOpportunitiesRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(OpportunitiesRepository.class);
    }

    default GuildMemberHistoryRepository getGuildMemberHistoryRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(GuildMemberHistoryRepository.class);

    }

    default DemographicsRepository getDemographicsRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(DemographicsRepository.class);
    }

    default MemberRoleRepository getMemberRoleRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberRoleRepository.class);
    }

    default PermissionRepository getPermissionRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(PermissionRepository.class);
    }

    default RolePermissionRepository getRolePermissionRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(RolePermissionRepository.class);
    }

    default EmailRepository getEmailRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(EmailRepository.class);
    }

    default OnboardingProfileRepository getOnboardingProfileRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(OnboardingProfileRepository.class);
    }
    default EmploymentDesiredAvailabilityRepository getEmploymentDesiredAvailabilityRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(EmploymentDesiredAvailabilityRepository.class);
    }

    default ReferralTypeRepository getReferralTypeRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(ReferralTypeRepository.class);
    default OnboardeeEmploymentEligibilityRepository getOnboardeeEmploymentEligibilityRepository(){
        return getEmbeddedServer().getApplicationContext().getBean(OnboardeeEmploymentEligibilityRepository.class);
    }

    default BackgroundInformationRepository getBackgroundInformationRepository(){
        return getEmbeddedServer().getApplicationContext().getBean(BackgroundInformationRepository.class);
    }
}
