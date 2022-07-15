package com.objectcomputing.onboard.services.fixture;

import io.micronaut.runtime.server.EmbeddedServer;

public interface RepositoryFixture {
    EmbeddedServer getEmbeddedServer();

//    default EmailRepository getEmailRepository() {
//        return getEmbeddedServer().getApplicationContext().getBean(EmailRepository.class);
//    }
}
