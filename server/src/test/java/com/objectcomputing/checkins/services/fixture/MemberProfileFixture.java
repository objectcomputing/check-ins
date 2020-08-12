package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Transactional(Transactional.TxType.NEVER)
public interface MemberProfileFixture extends RepositoryFixture {

    default MemberProfile createADefaultMemberProfile() {
        return getMemberProfileRepository().save(new MemberProfile("Mr. Bill", "Comedic Relief",
                null, "New York, New York", "billm@objectcomputing.com", "mr-bill-insperity",
                LocalDate.now(), "is a clay figurine clown star of a parody of children's clay animation shows"));
    }
}
