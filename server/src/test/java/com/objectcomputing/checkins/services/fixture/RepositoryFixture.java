package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.action_item.ActionItemRepository;
import com.objectcomputing.checkins.services.checkindocument.CheckinDocumentRepository;
import com.objectcomputing.checkins.services.checkin_notes.CheckinNoteRepository;
import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseRepository;
import com.objectcomputing.checkins.services.skills.SkillRepository;
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

}