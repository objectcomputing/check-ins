package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.certification.CertificationRepository;
import com.objectcomputing.checkins.services.certification.EarnedCertificationRepository;
import com.objectcomputing.checkins.services.document.DocumentRepository;
import com.objectcomputing.checkins.services.document.RoleDocumentationRepository;
import com.objectcomputing.checkins.services.email.EmailRepository;
import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.agenda_item.AgendaItemRepository;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNoteRepository;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentRepository;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.demographics.DemographicsRepository;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionRepository;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerRepository;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestRepository;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateRepository;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberHistoryRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.memberprofile.csvreport.MemberProfileReportRepository;
import com.objectcomputing.checkins.services.private_notes.PrivateNoteRepository;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseRepository;
import com.objectcomputing.checkins.services.questions.QuestionRepository;
import com.objectcomputing.checkins.services.reviews.ReviewAssignmentRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.role.member_roles.MemberRoleRepository;
import com.objectcomputing.checkins.services.role.role_permissions.RolePermissionRepository;
import com.objectcomputing.checkins.services.settings.SettingsRepository;
import com.objectcomputing.checkins.services.skillcategory.SkillCategoryRepository;
import com.objectcomputing.checkins.services.skillcategory.skillcategory_skill.SkillCategorySkillRepository;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.services.team.member.MemberHistoryRepository;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
import com.objectcomputing.checkins.services.volunteering.VolunteeringEventRepository;
import com.objectcomputing.checkins.services.volunteering.VolunteeringOrganizationRepository;
import com.objectcomputing.checkins.services.volunteering.VolunteeringRelationshipRepository;
import io.micronaut.runtime.server.EmbeddedServer;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursRepository;
import com.objectcomputing.checkins.services.slack.kudos.AutomatedKudosRepository;

public interface RepositoryFixture {
    EmbeddedServer getEmbeddedServer();

    default MemberHistoryRepository getMemberHistoryRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberHistoryRepository.class);
    }

    default FeedbackTemplateRepository getFeedbackTemplateRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(FeedbackTemplateRepository.class);
    }

    default TemplateQuestionRepository getTemplateQuestionRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(TemplateQuestionRepository.class);
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

    default EmployeeHoursRepository getEmployeeHoursRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(EmployeeHoursRepository.class);
    }

    default FeedbackAnswerRepository getFeedbackAnswerRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(FeedbackAnswerRepository.class);
    }

     default SettingsRepository getSettingsRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SettingsRepository.class);
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

    default RolePermissionRepository getRolePermissionRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(RolePermissionRepository.class);
    }

    default EmailRepository getEmailRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(EmailRepository.class);
    }

    default ReviewPeriodRepository getReviewPeriodRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(ReviewPeriodRepository.class);
    }

    default KudosRepository getKudosRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(KudosRepository.class);
    }

    default KudosRecipientRepository getKudosRecipientRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(KudosRecipientRepository.class);
    }

    default ReviewAssignmentRepository getReviewAssignmentRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(ReviewAssignmentRepository.class);
    }

    default SkillCategoryRepository getSkillCategoryRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SkillCategoryRepository.class);
    }

    default SkillCategorySkillRepository getSkillCategorySkillRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SkillCategorySkillRepository.class);
    }

    default MemberProfileReportRepository getMemberProfileReportRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberProfileReportRepository.class);
    }

    default CertificationRepository getCertificationRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(CertificationRepository.class);
    }

    default EarnedCertificationRepository getEarnedCertificationRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(EarnedCertificationRepository.class);
    }

    default VolunteeringOrganizationRepository getVolunteeringOrganizationRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(VolunteeringOrganizationRepository.class);
    }

    default VolunteeringRelationshipRepository getVolunteeringRelationshipRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(VolunteeringRelationshipRepository.class);
    }

    default VolunteeringEventRepository getVolunteeringEventRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(VolunteeringEventRepository.class);
    }

    default DocumentRepository getDocumentRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(DocumentRepository.class);
    }

    default RoleDocumentationRepository getRoleDocumentationRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(RoleDocumentationRepository.class);
    }

    default AutomatedKudosRepository getAutomatedKudosRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(AutomatedKudosRepository.class);
    }
}
