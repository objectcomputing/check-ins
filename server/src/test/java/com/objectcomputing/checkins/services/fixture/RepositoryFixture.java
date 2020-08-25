package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.checkins.CheckInRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
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

    default SkillRepository getSkillRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(SkillRepository.class);
    }

    default CheckInRepository getCheckInRepository(){
        return getEmbeddedServer().getApplicationContext().getBean(CheckInRepository.class);
    }
}