package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.onboard.workingenvironment.WorkingEnvironment;
import com.objectcomputing.checkins.services.onboardeecreate.newhire.model.NewHireAccountEntity;

public interface NewHireAccountFixture extends RepositoryFixture {
    default NewHireAccountEntity createNewHireAccountEntity(){
        return getNewHireAccountRepository()
                //I still need to add the items in the constructor for this fixture creation
                .save(new NewHireAccountEntity (null)
                .block();

    };

}
