package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.role.RoleRepository;
import io.micronaut.runtime.server.EmbeddedServer;

public interface RepositoryFixture {
    EmbeddedServer getEmbeddedServer();

    default MemberProfileRepository getMemberProfileRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(MemberProfileRepository.class);
    }

    default RoleRepository getRoleRepository() {
        return getEmbeddedServer().getApplicationContext().getBean(RoleRepository.class);
    }
}