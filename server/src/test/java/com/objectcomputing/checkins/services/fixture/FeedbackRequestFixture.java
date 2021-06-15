package com.objectcomputing.checkins.services.fixture;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import java.time.LocalDate;

import java.util.UUID;

//      this.id = id;
//              this.creatorId = creatorId;
//              this.requesteeId = requesteeId;
//              this.templateId = templateId;
//              this.sendDate = sendDate;
//              this.dueDate = dueDate;
//              this.status = status;\
public interface FeedbackRequestFixture extends RepositoryFixture {

    default FeedbackRequest createFeedbackRequest(MemberProfile memberProfile, MemberProfile memberProfileForPDL) {
        LocalDate testDate = LocalDate.of(2010, 10, 8);
        return getFeedbackRequestRepository().save(new FeedbackRequest(UUID.randomUUID(), memberProfileForPDL.getId(), memberProfile.getId(), UUID.randomUUID(), testDate, null, "pending"));
    }

}
