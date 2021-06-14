package com.objectcomputing.checkins.services.feedback_request;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RepositoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.inject.Inject;

import java.time.LocalDate;
import java.util.UUID;

import static com.objectcomputing.checkins.services.memberprofile.MemberProfileTestUtil.mkMemberProfile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FeedbackRequestControllerTest extends TestContainersSuite implements RepositoryFixture, MemberProfileFixture, FeedbackRequestFixture, RoleFixture {
    @Inject
    @Client("/services/feedback/requests")
    HttpClient client;

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackRequestControllerTest.class);

    @Test
    void testCreateFeedbackRequestByAdmin() {
        //create two member profiles: one for normal employee, one for admin
        final MemberProfile memberProfile = createADefaultMemberProfile();
        final MemberProfile admin = getMemberProfileRepository().save(mkMemberProfile("admin"));
        createDefaultAdminRole(admin);

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(admin.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(response.body().getCreatorId(), admin.getId());
        assertEquals(response.body().getRequesteeId(), memberProfile.getId());
        assertEquals(response.body().getDueDate(), dto.getDueDate());
        assertEquals(response.body().getTemplateId(), dto.getTemplateId());
        assertEquals(response.body().getStatus(), dto.getStatus());
        assertEquals(response.body().getSendDate(), dto.getSendDate());
    }

    @Test
    void testCreateFeedbackRequestByAssignedPDL() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfileForPdl(memberProfile);
        createDefaultRole(RoleType.PDL, memberProfileForPDL);

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
                .basicAuth(memberProfileForPDL.getWorkEmail(), RoleType.Constants.PDL_ROLE);
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
    void testCreateFeedbackRequestByOtherMember() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileTwo = createADefaultMemberProfile();

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(memberProfileTwo.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(memberProfileTwo.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.FORBIDDEN, response.getStatus());

    }

    @Test
    void testCreateFeedbackRequestByUnassignedPdl() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile memberProfileForPDL = createADefaultMemberProfile();
        createDefaultRole(RoleType.PDL, memberProfileForPDL);

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
                .basicAuth(memberProfileForPDL.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.FORBIDDEN, response.getStatus());
//        assertEquals(response.body().getCreatorId(), memberProfileForPDL.getId());
//        assertEquals(response.body().getRequesteeId(), memberProfile.getId());
//        assertEquals(response.body().getDueDate(), dto.getDueDate());
//        assertEquals(response.body().getTemplateId(), dto.getTemplateId());
//        assertEquals(response.body().getStatus(), dto.getStatus());
//        assertEquals(response.body().getSendDate(), dto.getSendDate());

    }

    @Test
    void testCreateFeedbackRequestByMember() {
        //create two member profiles: one for normal employee, one for PDL of normal employee
        MemberProfile memberProfile = createADefaultMemberProfile();
        MemberProfile requesteeProfile = createADefaultMemberProfile();

        //create feedback request
        final FeedbackRequestCreateDTO dto = new FeedbackRequestCreateDTO();
        dto.setCreatorId(requesteeProfile.getId());
        dto.setRequesteeId(memberProfile.getId());
        dto.setSendDate(LocalDate.now());
        dto.setTemplateId(UUID.randomUUID());
        dto.setStatus("pending");
        dto.setDueDate(null);

        //send feedback request
        final HttpRequest<?> request = HttpRequest.POST("", dto)
                .basicAuth(requesteeProfile.getWorkEmail(), RoleType.Constants.PDL_ROLE);
        final HttpResponse<FeedbackRequestResponseDTO> response = client.toBlocking().exchange(request, FeedbackRequestResponseDTO.class);

        //assert that content of some feedback request equals the test
        assertEquals(HttpStatus.FORBIDDEN, response.getStatus());

    }

}
