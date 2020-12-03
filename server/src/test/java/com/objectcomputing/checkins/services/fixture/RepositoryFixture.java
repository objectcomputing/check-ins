package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.agenda_item.AgendaItemRepository;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentRepository;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNoteRepository;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.guild.GuildRepository;
import com.objectcomputing.checkins.services.guild.member.GuildMemberRepository;
import com.objectcomputing.checkins.services.member_skill.MemberSkillRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.questions.QuestionRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseRepository;
import com.objectcomputing.checkins.services.skills.SkillRepository;
import com.objectcomputing.checkins.services.skills.tags.SkillSkillTagLookupRepository;
import com.objectcomputing.checkins.services.skills.tags.SkillTagRepository;
import com.objectcomputing.checkins.services.team.TeamRepository;
import com.objectcomputing.checkins.services.team.member.TeamMemberRepository;
import io.micronaut.runtime.server.EmbeddedServer;

public interface RepositoryFixture {
    EmbeddedServer getEmbeddedServer();

    default MemberProfileRepository getMemberProfileRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberProfileRepository.class);
    }

    default RoleRepository getRoleRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(RoleRepository.class);
    }

    default PulseResponseRepository getPulseResponseRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(PulseResponseRepository.class);
    }

    default SkillSkillTagLookupRepository getSkillTagLookupRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SkillSkillTagLookupRepository.class);
    }

    default SkillTagRepository getSkillTagRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SkillTagRepository.class);
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

}