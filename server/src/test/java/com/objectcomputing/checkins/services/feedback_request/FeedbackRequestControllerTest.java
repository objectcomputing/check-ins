package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestController;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.lang.reflect.Member;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;

public class FeedbackRequestControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture, FeedbackRequestFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/request")
    HttpClient client;

    @Test
    void testCreateFeedbackRequestByAdmin() {

    }

    @Test
    void testCreateFeedbackRequestByAdmin() {
        //create two member profiles: one for normal employee, one for pdl of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        Role authRole = createDefaultRole(RoleType.PDL, memberProfileForPDL);
        getMemberProfileRepository().save(memberProfile);
        getMemberProfileRepository().save(memberProfileForPDL);

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();

        dto.setCreatorId(memberProfileForPDL.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileForPDL.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(response.body().getCreatorId(), memberProfileForPDL.getId());
        assertEquals(response.body().getRequesteeId(), memberProfile.getId());
        assertEquals(response.body().getDueDate(), dto.getDueDate());
        assertEquals(response.body().getTemplateId(), dto.getTemplateId());
        assertEquals(response.body().getStatus(), dto.getStatus());
        assertEquals(response.body().getSendDate(), dto.getSendDate());

    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdl() {

    }

    @Test
    void testCreateFeedbackRequestByMember() {

    }


}
