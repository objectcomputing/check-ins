package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironment;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireCredentialsEntity;

import java.time.Instant;

import static com.objectcomputing.checkins.services.onboardeecreate.commons.AccountState.Pending;

public interface NewHireAccountFixture extends RepositoryFixture {
    default NewHireAccountEntity createNewHireAccountEntity(){
        return getNewHireAccountRepository()
                .save(new NewHireAccountEntity ("Mr.Nice@gmail.com", Pending, Instant.now(),null))
//                .block()
                ;

    }

}


