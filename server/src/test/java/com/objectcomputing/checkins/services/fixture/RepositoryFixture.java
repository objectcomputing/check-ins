package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import com.objectcomputing.checkins.services.pulseresponse.PulseResponseRepository;
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
}