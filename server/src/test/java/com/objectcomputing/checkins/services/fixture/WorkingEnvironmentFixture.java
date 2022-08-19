package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.workingenvironment.WorkingEnvironment;

import reactor.core.publisher.Mono;

public interface WorkingEnvironmentFixture extends RepositoryFixture {
    default Mono<WorkingEnvironment> createWorkingEnvironment() {
        return getWorkingEnvironmentRespository()
                .save(new WorkingEnvironment("Remote", "Key Fob", "Mac", "HDMI Cable", "No I'm good :)", null));
    }
}
