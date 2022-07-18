package com.objectcomputing.checkins.services.document;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.document.role_document.RoleDocument;
import com.objectcomputing.checkins.services.document.role_document.RoleDocumentResponseDTO;
import com.objectcomputing.checkins.services.fixture.DocumentFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RoleDocumentControllerTest extends TestContainersSuite implements DocumentFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/documents/role-documents")
    HttpClient client;

    @Test
    void testCreateRoleDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);
        Document document = saveDefaultDocument();
        Map<String, UUID> args = Map.of("roleId", adminRole.getId(), "documentId", document.getId());

        final HttpRequest<?> request = HttpRequest.POST("", args)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<RoleDocument> response = client.toBlocking().exchange(request, RoleDocument.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(adminRole.getId(), response.getBody().get().getRoleDocumentId().getRoleId());
        assertEquals(document.getId(), response.getBody().get().getRoleDocumentId().getDocumentId());
    }

    @Test
    void testCreateRoleDocumentNotAuthorized() {
        MemberProfile member = createADefaultMemberProfile();
        Role memberRole = createRole(RoleType.MEMBER);
        Document document = saveDefaultDocument();
        Map<String, UUID> args = Map.of("roleId", memberRole.getId(), "documentId", document.getId());


        final HttpRequest<?> request = HttpRequest.POST("", args)
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateRoleDocumentWithInvalidRole() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = saveDefaultDocument();
        UUID nonexistentRoleId = UUID.randomUUID();
        Map<String, UUID> args = Map.of("roleId", nonexistentRoleId, "documentId", document.getId());

        final HttpRequest<?> request = HttpRequest.POST("", args)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Cannot save role document with nonexistent role id %s", nonexistentRoleId), responseException.getMessage());
    }

    @Test
    void testCreateRoleDocumentWithInvalidDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);
        UUID nonexistentDocumentId = UUID.randomUUID();
        Map<String, UUID> args = Map.of("roleId", adminRole.getId(), "documentId", nonexistentDocumentId);

        final HttpRequest<?> request = HttpRequest.POST("", args)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Cannot save role document with nonexistent document id %s", nonexistentDocumentId), responseException.getMessage());
    }

    @Test
    void testCreateDuplicateRoleDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);
        RoleDocument savedRoleDocument = saveDefaultRoleDocument(adminRole);
        RoleDocument roleDocument = new RoleDocument(savedRoleDocument.getRoleDocumentId().getRoleId(), savedRoleDocument.getRoleDocumentId().getDocumentId(), 1);
        Map<String, UUID> args = Map.of("roleId", savedRoleDocument.getRoleDocumentId().getRoleId(), "documentId", savedRoleDocument.getRoleDocumentId().getDocumentId());

        final HttpRequest<?> request = HttpRequest.POST("", args)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
        assertEquals(String.format("There already exists a role document with %s", roleDocument.getRoleDocumentId()), responseException.getMessage());
    }

    @Test
    void testGetDocumentsByRole() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);
        Role pdlRole = createRole(RoleType.PDL);
        Document document1 = getDocumentRepository().save(new Document("Info Document", "Information", "/pdf/info.pdf"));
        Document document2 = getDocumentRepository().save(new Document("Data Document", "Data", "/pdf/data.pdf"));
        Document document3 = getDocumentRepository().save(new Document("PDL Document", "For PDLs", "/pdf/pdl.pdf"));
        RoleDocument roleDocument1 = getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), document1.getId(), 1));
        RoleDocument roleDocument2 = getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), document2.getId(), 2));
        getRoleDocumentRepository().save(new RoleDocument(pdlRole.getId(), document3.getId(), 1));

        final MutableHttpRequest<?> request = HttpRequest.GET(String.format("/%s", adminRole.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<DocumentResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(DocumentResponseDTO.class));

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(2, response.getBody().get().size());

        DocumentResponseDTO res1 = response.getBody().get().get(0);
        assertEquals(document1.getId(), res1.getId());
        assertEquals(document1.getName(), res1.getName());
        assertEquals(document1.getDescription(), res1.getDescription());
        assertEquals(document1.getUrl(), res1.getUrl());
        assertEquals(roleDocument1.getDocumentNumber(), res1.getDocumentNumber());

        DocumentResponseDTO res2 = response.getBody().get().get(1);
        assertEquals(document2.getId(), res2.getId());
        assertEquals(document2.getName(), res2.getName());
        assertEquals(document2.getDescription(), res2.getDescription());
        assertEquals(document2.getUrl(), res2.getUrl());
        assertEquals(roleDocument2.getDocumentNumber(), res2.getDocumentNumber());
    }

    @Test
    void testGetDocumentsByRoleUnauthorized() {
        MemberProfile member = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);

        final MutableHttpRequest<?> request = HttpRequest.GET(String.format("/%s", adminRole.getId()))
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals(String.format("You are not allowed to access documents for role id %s", adminRole.getId()), responseException.getMessage());
    }

    @Test
    void testGetDocumentsByRoleInvalidRole() {
        MemberProfile member = createADefaultMemberProfile();
        UUID nonexistentRoleId = UUID.randomUUID();

        final MutableHttpRequest<?> request = HttpRequest.GET(String.format("/%s", nonexistentRoleId))
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Cannot get documents for nonexistent role id %s", nonexistentRoleId), responseException.getMessage());
    }

    @Test
    void testUpdateRoleDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);

        Document document1 = getDocumentRepository().save(new Document("Tutorial Document", "Tutorial", "/pdf/tutorial.pdf"));
        RoleDocument roleDocument1 = getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), document1.getId(), 1));
        Document document2 = getDocumentRepository().save(new Document("Info Document", "Information", "/pdf/info.pdf"));
        getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), document2.getId(), 2));
        Document document3 = getDocumentRepository().save(new Document("Data Document", "Data", "/pdf/data.pdf"));
        getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), document3.getId(), 3));

        // Reorder the documents
        roleDocument1.setDocumentNumber(2);

        final HttpRequest<RoleDocument> request = HttpRequest.PUT("", roleDocument1)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<DocumentResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(DocumentResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(3, response.getBody().get().size());

        DocumentResponseDTO firstRes = response.getBody().get().get(0);
        assertEquals(document2.getId(), firstRes.getId());
        assertEquals(document2.getName(), firstRes.getName());
        assertEquals(1, firstRes.getDocumentNumber());

        DocumentResponseDTO secondRes = response.getBody().get().get(1);
        assertEquals(document1.getId(), secondRes.getId());
        assertEquals(document1.getName(), secondRes.getName());
        assertEquals(2, secondRes.getDocumentNumber());

        DocumentResponseDTO thirdRes = response.getBody().get().get(2);
        assertEquals(document3.getId(), thirdRes.getId());
        assertEquals(document3.getName(), thirdRes.getName());
        assertEquals(3, thirdRes.getDocumentNumber());
    }

    @Test
    void testUpdateRoleDocumentNotAuthorized() {
        MemberProfile member = createADefaultMemberProfile();
        Role memberRole = createRole(RoleType.MEMBER);
        RoleDocument roleDocument = saveDefaultRoleDocument(memberRole);
        roleDocument.setDocumentNumber(2);

        final HttpRequest<RoleDocument> request = HttpRequest.PUT("", roleDocument)
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateRoleDocumentWithInvalidId() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);
        saveDefaultRoleDocument(adminRole);
        RoleDocument roleDocument = new RoleDocument(UUID.randomUUID(), UUID.randomUUID(), 2);

        final HttpRequest<RoleDocument> request = HttpRequest.PUT("", roleDocument)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Cannot update role document with nonexistent id %s", roleDocument.getRoleDocumentId()), responseException.getMessage());
    }

    @Test
    void testDeleteRoleDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);
        RoleDocument roleDocument = saveDefaultRoleDocument(adminRole);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s/%s", adminRole.getId(), roleDocument.getRoleDocumentId().getDocumentId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<?> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testDeleteRoleDocumentNotAuthorized() {
        MemberProfile member = createADefaultMemberProfile();
        Role memberRole = createRole(RoleType.MEMBER);
        RoleDocument roleDocument = saveDefaultRoleDocument(memberRole);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s/%s", memberRole.getId(), roleDocument.getRoleDocumentId().getDocumentId()))
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testDeleteDocumentWithInvalidId() {
        MemberProfile admin = createADefaultMemberProfile();
        RoleDocument roleDocument = new RoleDocument(UUID.randomUUID(), UUID.randomUUID(), 1);

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s/%s", roleDocument.getRoleDocumentId().getRoleId(), roleDocument.getRoleDocumentId().getDocumentId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals(String.format("Cannot delete role document with nonexistent id %s", roleDocument.getRoleDocumentId()), responseException.getMessage());
    }

    @Test
    void testGetAllDocuments() {
        MemberProfile admin = createADefaultMemberProfile();
        Role adminRole = createRole(RoleType.ADMIN);
        Role pdlRole = createRole(RoleType.PDL);
        Role memberRole = createRole(RoleType.MEMBER);

        Document tutorialDocument = getDocumentRepository().save(new Document("Tutorial Document", "Tutorial", "/pdf/tutorial.pdf"));
        Document infoDocument = getDocumentRepository().save(new Document("Info Document", "Information", "/pdf/info.pdf"));
        Document dataDocument = getDocumentRepository().save(new Document("Data Document", "Data", "/pdf/data.pdf"));

        // Assign all documents to admins
        RoleDocument adminDoc1 = getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), tutorialDocument.getId(), 1));
        RoleDocument adminDoc2 = getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), infoDocument.getId(), 2));
        RoleDocument adminDoc3 = getRoleDocumentRepository().save(new RoleDocument(adminRole.getId(), dataDocument.getId(), 3));

        // Assign two documents to PDLs
        RoleDocument pdlDoc1 = getRoleDocumentRepository().save(new RoleDocument(pdlRole.getId(), infoDocument.getId(), 1));
        RoleDocument pdlDoc2 = getRoleDocumentRepository().save(new RoleDocument(pdlRole.getId(), tutorialDocument.getId(), 2));

        // Assign one document to members
        RoleDocument memberDoc1 = getRoleDocumentRepository().save(new RoleDocument(memberRole.getId(), tutorialDocument.getId(), 1));

        final MutableHttpRequest<?> request = HttpRequest.GET("")
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<List<RoleDocumentResponseDTO>> response = client.toBlocking().exchange(request, Argument.listOf(RoleDocumentResponseDTO.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertTrue(response.getBody().isPresent());
        assertEquals(3, response.getBody().get().size());

        RoleDocumentResponseDTO firstRes = response.getBody().get().get(0);
        assertEquals(dataDocument.getId(), firstRes.getId());
        assertEquals(dataDocument.getName(), firstRes.getName());
        assertEquals(1, firstRes.getRoles().size());
        assertEquals(adminDoc3, firstRes.getRoles().get(0));

        RoleDocumentResponseDTO secondRes = response.getBody().get().get(1);
        assertEquals(infoDocument.getId(), secondRes.getId());
        assertEquals(infoDocument.getName(), secondRes.getName());
        assertEquals(2, secondRes.getRoles().size());
        assertEquals(adminDoc2, secondRes.getRoles().get(0));
        assertEquals(pdlDoc1, secondRes.getRoles().get(1));

        RoleDocumentResponseDTO thirdRes = response.getBody().get().get(2);
        assertEquals(tutorialDocument.getId(), thirdRes.getId());
        assertEquals(tutorialDocument.getName(), thirdRes.getName());
        assertEquals(3, thirdRes.getRoles().size());
        assertEquals(adminDoc1, thirdRes.getRoles().get(0));
        assertEquals(pdlDoc2, thirdRes.getRoles().get(1));
        assertEquals(memberDoc1, thirdRes.getRoles().get(2));
    }

    @Test
    void testGetAllDocumentsNotAuthorized() {
        MemberProfile member = createADefaultMemberProfile();

        final MutableHttpRequest<?> request = HttpRequest.GET("")
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
    }
}
