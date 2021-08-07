package com.objectcomputing.checkins.services.rale;


import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.RaleFixture;
import com.objectcomputing.checkins.services.fixture.RaleMemberFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.rale.Rale;
import com.objectcomputing.checkins.services.rale.RaleCreateDTO;
import com.objectcomputing.checkins.services.rale.RaleResponseDTO;
import com.objectcomputing.checkins.services.rale.RaleUpdateDTO;
import com.objectcomputing.checkins.services.rale.member.RaleMember;
import com.objectcomputing.checkins.services.rale.member.RaleMemberUpdateDTO;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.*;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RaleControllerTest extends TestContainersSuite implements RaleFixture, MemberProfileFixture, RoleFixture, RaleMemberFixture {

    @Inject
    @Client("/services/rales")
    HttpClient client;

    @Test
    void testCreateARale() {
        RaleCreateDTO raleCreateDTO = new RaleCreateDTO();
        raleCreateDTO.setRale(RaleType.ADMIN);
        raleCreateDTO.setDescription("description");
        MemberProfile memberProfile = createADefaultMemberProfile();
        raleCreateDTO.setRaleMembers(List.of(new RaleCreateDTO.RaleMemberCreateDTO(memberProfile.getId(), true)));

        final HttpRequest<RaleCreateDTO> request = HttpRequest.POST("", raleCreateDTO).basicAuth(memberProfile.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse<RaleResponseDTO> response = client.toBlocking().exchange(request, RaleResponseDTO.class);

        RaleResponseDTO raleEntity = response.body();

        assertEquals(raleCreateDTO.getDescription(), raleEntity.getDescription());
        assertEquals(raleCreateDTO.getRale(), raleEntity.getRale());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), raleEntity.getId()), response.getHeaders().get("location"));

    }

    @Test
    void testCreateRaleNoLeads() {
        RaleCreateDTO raleCreateDTO = new RaleCreateDTO();
        raleCreateDTO.setRale(RaleType.ADMIN);
        raleCreateDTO.setDescription("description");
        raleCreateDTO.setRaleMembers(new ArrayList<>());

        final HttpRequest<RaleCreateDTO> request = HttpRequest.POST("", raleCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);

        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Rale must include at least one rale lead", errors.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(request.getPath(), href.asText());
    }

    @Test
    void testCreateAnInvalidRale() {
        RaleCreateDTO raleCreateDTO = new RaleCreateDTO();

        final HttpRequest<RaleCreateDTO> request = HttpRequest.POST("", raleCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("rale.rale: must not be null", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateANullRale() {

        final HttpRequest<String> request = HttpRequest.POST("", "").basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [rale] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testCreateARaleWithExistingName() {

        Rale raleEntity = createDefaultRale();

        RaleCreateDTO raleCreateDTO = new RaleCreateDTO();
        raleCreateDTO.setDescription("test");
        raleCreateDTO.setRale(raleEntity.getRale());
        raleCreateDTO.setRaleMembers(new ArrayList<>());
        MemberProfile memberProfile = createADefaultMemberProfile();
        raleCreateDTO.setRaleMembers(List.of(new RaleCreateDTO.RaleMemberCreateDTO(memberProfile.getId(), true)));

        final HttpRequest<RaleCreateDTO> request = HttpRequest.POST("", raleCreateDTO).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(request.getPath(), href);
        assertEquals(String.format("Rale with name %s already exists", raleCreateDTO.getRale()), error);
    }

    @Test
    void testReadRale() {
        Rale raleEntity = createDefaultRale() ;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", raleEntity.getId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<RaleResponseDTO> response = client.toBlocking().exchange(request, RaleResponseDTO.class);

        assertEntityDTOEqual(raleEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void testReadRaleNotFound() {

        final HttpRequest<?> request = HttpRequest.GET(String.format("/%s", UUID.randomUUID())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Rale.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
    }

    @Test
    void testFindAllRales() {

        Rale raleEntity = createDefaultRale();

        final HttpRequest<?> request = HttpRequest.GET(String.format("/")).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RaleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RaleResponseDTO.class));

        assertEntityDTOEqual(Set.of(raleEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByName() {
        Rale raleEntity = createDefaultRale() ;

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?rale=%s", raleEntity.getRale())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RaleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RaleResponseDTO.class));

        assertEntityDTOEqual(Set.of(raleEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindByMemberId() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Rale raleEntity = createDefaultRale();

        RaleMember raleMemberEntity = createDefaultRaleMember(raleEntity, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?memberid=%s", raleMemberEntity.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RaleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RaleResponseDTO.class));

        assertEntityDTOEqual(Set.of(raleEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testFindRales() {
        MemberProfile memberProfile = createADefaultMemberProfile();
        Rale raleEntity = createDefaultRale();

        RaleMember raleMemberEntity = createDefaultRaleMember(raleEntity, memberProfile);

        final HttpRequest<?> request = HttpRequest.GET(String.format("/?rale=%s&memberid=%s", raleEntity.getRale(),
                raleMemberEntity.getMemberId())).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpResponse<Set<RaleResponseDTO>> response = client.toBlocking().exchange(request, Argument.setOf(RaleResponseDTO.class));

        assertEntityDTOEqual(Set.of(raleEntity), response.body());
        assertEquals(HttpStatus.OK, response.getStatus());

    }

    @Test
    void testUpdatePermissionDenied() {
        Rale raleEntity = createDefaultRale();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RaleUpdateDTO requestBody = updateFromEntity(raleEntity);
        RaleUpdateDTO.RaleMemberUpdateDTO newMember = updateDefaultRaleMemberDto(raleEntity, memberProfile, true);
        newMember.setLead(true);
        requestBody.setRaleMembers(Collections.singletonList(newMember));

        final HttpRequest<RaleUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(request.getPath(), href);
        assertEquals("You are not authorized to perform this operation", error);
    }

    @Test
    void testUpdateRaleSuccess() {
        MemberProfile user = createAnUnrelatedUser();
        createDefaultAdminRole(user);

        Rale raleEntity = createDefaultRale();
        MemberProfile memberProfile = createADefaultMemberProfile();

        RaleUpdateDTO requestBody = updateFromEntity(raleEntity);
        RaleUpdateDTO.RaleMemberUpdateDTO newMember = updateDefaultRaleMemberDto(raleEntity, memberProfile, true);
        newMember.setLead(true);
        requestBody.setRaleMembers(Collections.singletonList(newMember));

        final HttpRequest<RaleUpdateDTO> request = HttpRequest.PUT("/", requestBody).basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse<RaleResponseDTO> response = client.toBlocking().exchange(request, RaleResponseDTO.class);

        assertEntityDTOEqual(raleEntity, response.body());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), raleEntity.getId()), response.getHeaders().get("location"));
    }

    @Test
    void testUpdateRaleNullName() {
        Rale raleEntity = createDefaultRale();

        RaleUpdateDTO requestBody = new RaleUpdateDTO(raleEntity.getId(), null, null);
        requestBody.setRaleMembers(new ArrayList<>());

        final HttpRequest<RaleUpdateDTO> request = HttpRequest.PUT("", requestBody)
                .basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("rale.rale: must not be blank", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }

    @Test
    void testUpdateANullRale() {
        final HttpRequest<String> request = HttpRequest.PUT("", "").basicAuth(PDL_ROLE, PDL_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        JsonNode href = Objects.requireNonNull(body).get("_links").get("self").get("href");
        assertEquals("Required Body [rale] not specified", errors.asText());
        assertEquals(request.getPath(), href.asText());
        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
    }


    @Test
    void testUpdateRaleNotExist() {
        MemberProfile user = createAnUnrelatedUser();
        createDefaultAdminRole(user);

        Rale raleEntity = createDefaultRale();
        UUID requestId = UUID.randomUUID();
        RaleUpdateDTO requestBody = new RaleUpdateDTO(requestId.toString(), raleEntity.getRale(), raleEntity.getDescription());
        requestBody.setRaleMembers(new ArrayList<>());

        final MutableHttpRequest<RaleUpdateDTO> request = HttpRequest.PUT("", requestBody)
                .basicAuth(user.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        String error = Objects.requireNonNull(body).get("message").asText();
        String href = Objects.requireNonNull(body).get("_links").get("self").get("href").asText();

        assertEquals(String.format("Rale ID %s does not exist, can't update.", requestId), error);
        assertEquals(request.getPath(), href);
    }

    @Test
    void deleteRaleByMember() {
        // setup rale
        Rale raleEntity = createDefaultRale();
        // create members
        MemberProfile memberProfileofRaleLeadEntity = createADefaultMemberProfile();
        MemberProfile memberProfileOfRaleMember = createADefaultMemberProfileForPdl(memberProfileofRaleLeadEntity);
        //add members to rale
        createLeadRaleMember(raleEntity, memberProfileofRaleLeadEntity);
        createDefaultRaleMember(raleEntity, memberProfileOfRaleMember);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", raleEntity.getId())).basicAuth(memberProfileOfRaleMember.getWorkEmail(), MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        assertEquals("You are not authorized to perform this operation", errors.asText());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    @Test
    void deleteRaleByAdmin() {
        // setup rale
        Rale raleEntity = createDefaultRale();
        // create members
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createDefaultAdminRole(memberProfileOfAdmin);

        //add members to rale
        createDefaultRaleMember(raleEntity, memberProfileOfAdmin);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", raleEntity.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        final HttpResponse response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteRaleByRaleLead() {
        // setup rale
        Rale raleEntity = createDefaultRale();
        // create members
        MemberProfile memberProfileofRaleLeadEntity = createADefaultMemberProfile();
        //add members to rale
        createLeadRaleMember(raleEntity, memberProfileofRaleLeadEntity);
        // createDefaultRaleMember(rale, memberProfileOfRaleMember);

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", raleEntity.getId())).basicAuth(memberProfileofRaleLeadEntity.getWorkEmail(), MEMBER_ROLE);
        final HttpResponse response = client.toBlocking().exchange(request);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void deleteRaleByUnrelatedUser() {
        // setup rale
        Rale raleEntity = createDefaultRale();
        // create members
        MemberProfile user = createAnUnrelatedUser();

        final MutableHttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", raleEntity.getId())).basicAuth(user.getWorkEmail(), MEMBER_ROLE);

        //throw error
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        JsonNode body = responseException.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode errors = Objects.requireNonNull(body).get("message");
        assertEquals("You are not authorized to perform this operation", errors.asText());
        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }

    private void assertEntityDTOEqual(Collection<Rale> entities, Collection<RaleResponseDTO> dtos) {
        assertEquals(entities.size(), dtos.size());
        Iterator<Rale> iEntity = entities.iterator();
        Iterator<RaleResponseDTO> iDTO = dtos.iterator();
        while (iEntity.hasNext() && iDTO.hasNext()) {
            assertEntityDTOEqual(iEntity.next(), iDTO.next());
        }
    }

    private void assertEntityDTOEqual(Rale entity, RaleResponseDTO dto) {
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getRale(), dto.getRale());
        assertEquals(entity.getDescription(), dto.getDescription());
    }
}

