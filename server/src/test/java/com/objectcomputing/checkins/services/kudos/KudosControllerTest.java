package com.objectcomputing.checkins.services.kudos;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.KudosFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.TeamFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

public class KudosControllerTest extends TestContainersSuite implements MemberProfileFixture, KudosFixture, TeamFixture{

    @Inject
    @Client("/services/kudos")
    HttpClient client;

    @Test
    void testCreateMemberKudos() {
        MemberProfile kudosSender = createADefaultMemberProfile();
        MemberProfile kudosRecipient = createASecondDefaultMemberProfile();
        LocalDate date = LocalDate.of(2023,1,3);
        List<MemberProfile> recipientList = new ArrayList<>();
        recipientList.add(kudosRecipient);
        String message = "message";
        KudosCreateDTO createDTO = new KudosCreateDTO(true, message, kudosSender.getId(), null, recipientList);

        final HttpRequest<KudosCreateDTO> request = HttpRequest.POST("/", createDTO).basicAuth(kudosSender.getWorkEmail(), MEMBER_ROLE);
        HttpResponse<Kudos> response = client.toBlocking().exchange(request, Kudos.class);

        Kudos memberKudos = response.body();

        assertNotNull(memberKudos);
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(message, memberKudos.getMessage());
        assertNull(memberKudos.getDateApproved());
        assertEquals(kudosSender.getId(), memberKudos.getSenderId());

    }

    @Test
    void testApproveKudos() {
        MemberProfile kudosSender = createADefaultMemberProfile();
        Kudos kudos = createMemberKudos(kudosSender);

        KudosUpdateDTO approveDto = new KudosUpdateDTO();
        approveDto.setKudosId(kudos.getId());
        approveDto.setApproved(true);

        final HttpRequest<KudosUpdateDTO> request = HttpRequest.PUT("/", approveDto).basicAuth(kudosSender.getWorkEmail(), ADMIN_ROLE);
        HttpResponse<Kudos> response = client.toBlocking().exchange(request, Kudos.class);

        Kudos memberKudos = response.body();

        assertNotNull(memberKudos);
        assertEquals(kudos.getMessage(), memberKudos.getMessage());
        assertEquals(kudos.getSenderId(), memberKudos.getSenderId());
        assertNotNull(memberKudos.getDateApproved());
    }

    @Test
    void testUpdateKudos() {
        MemberProfile kudosSender = createADefaultMemberProfile();
        Kudos kudos = createMemberKudos(kudosSender);
        String newMessage = "new message";

        KudosUpdateDTO approveDto = new KudosUpdateDTO();
        approveDto.setKudosId(kudos.getId());
        approveDto.setMessage(newMessage);

        final HttpRequest<KudosUpdateDTO> request = HttpRequest.PUT("/", approveDto).basicAuth(kudosSender.getWorkEmail(), MEMBER_ROLE);
        HttpResponse<Kudos> response = client.toBlocking().exchange(request, Kudos.class);

        Kudos memberKudos = response.body();

        assertNotNull(memberKudos);
        assertEquals(newMessage, memberKudos.getMessage());
        assertEquals(kudos.getSenderId(), memberKudos.getSenderId());
        assertNull(memberKudos.getDateApproved());
    }
    @Test
    void testGetKudosById() {
        MemberProfile kudosSender = createADefaultMemberProfile();
        Kudos kudos = createMemberKudos(kudosSender);

        HttpResponse<Kudos> response = client.toBlocking().exchange(HttpRequest.GET(String.format("/%s", kudos.getId())).basicAuth(kudosSender.getWorkEmail(), MEMBER_ROLE), Kudos.class);

        Kudos memberKudos = response.body();

        assertEquals(kudos, memberKudos);
    }

    @Test
    void testGetKudosByIdNotFound() {
        MemberProfile kudosSender = createADefaultMemberProfile();

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
                    client.toBlocking().exchange(HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(kudosSender.getWorkEmail(), MEMBER_ROLE));
                });


        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testSearchKudosByField() {

    }



    @Test
    void testDeleteKudosById() {
        MemberProfile kudosSender = createADefaultMemberProfile();
        Kudos kudos = createMemberKudos(kudosSender);

        HttpResponse response = client.toBlocking().exchange(HttpRequest.DELETE(String.format("/%s", kudos.getId())).basicAuth(kudosSender.getWorkEmail(), MEMBER_ROLE));

        assertEquals(HttpResponse.noContent().getStatus(), response.getStatus());
    }
}
