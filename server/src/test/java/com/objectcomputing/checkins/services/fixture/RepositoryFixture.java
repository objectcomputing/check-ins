package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.agenda_item.AgendaItemRepository;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNoteRepository;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentRepository;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.feedback.FeedbackRepository;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.private_notes.PrivateNoteRepository;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseRepository;
import com.objectcomputing.checkins.services.question_category.QuestionCategoryRepository;
import com.objectcomputing.checkins.services.questions.QuestionRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import com.objectcomputing.checkins.services.tags.entityTag.EntityTagRepository;
import com.objectcomputing.checkins.services.tags.TagRepository;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
import io.micronaut.runtime.server.EmbeddedServer;
import com.objectcomputing.checkins.services.survey.SurveyRepository;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursRepository;


public interface RepositoryFixture {
    EmbeddedServer getEmbeddedServer();

    default TagRepository getTagRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(TagRepository.class);
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

    default FeedbackRepository getFeedbackRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(FeedbackRepository.class);
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
}
