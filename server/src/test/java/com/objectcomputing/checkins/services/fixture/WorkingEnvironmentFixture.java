package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironment;

import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import reactor.core.publisher.Mono;

public interface WorkingEnvironmentFixture extends RepositoryFixture {
    default WorkingEnvironment createWorkingEnvironment(NewHireAccountEntity newHireAccountEntity) {
        return getWorkingEnvironmentRespository()
                .save(new WorkingEnvironment("Remote", "Key Fob", "Mac", "HDMI Cable",
                        "No I'm good :)", newHireAccountEntity ))

                ;
    }
}
