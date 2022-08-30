package com.objectcomputing.checkins.services.document;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.DocumentFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.RoleType;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentControllerTest extends TestContainersSuite implements DocumentFixture, MemberProfileFixture {

    @Inject
    @Client("/services/documents")
    HttpClient client;

    @Test
    void testCreateDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = createDefaultDocument();

        final HttpRequest<Document> request = HttpRequest.POST("", document)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<Document> response = client.toBlocking().exchange(request, Document.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getBody().get().getId());
        assertEquals(document.getName(), response.getBody().get().getName());
        assertEquals(document.getDescription(), response.getBody().get().getDescription());
        assertEquals(document.getUrl(), response.getBody().get().getUrl());
    }

    @Test
    void testCreateDocumentNotAuthorized() {
        MemberProfile member = createADefaultMemberProfile();
        Document document = createDefaultDocument();

        final HttpRequest<Document> request = HttpRequest.POST("", document)
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testCreateDocumentWithDuplicateName() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = saveDefaultDocument();
        Document newDocument = new Document(document.getName(), "A document with a duplicate name", "/pdf/duplicate.pdf");

        final HttpRequest<Document> request = HttpRequest.POST("", newDocument)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
        assertEquals(String.format("A document with the name '%s' already exists", document.getName()), responseException.getMessage());
    }

    @Test
    void testCreateDocumentWithBlankName() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = createDefaultDocument();
        document.setName(" ");

        final HttpRequest<Document> request = HttpRequest.POST("", document)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Document name must not be blank", responseException.getMessage());
    }

    @Test
    void testGetDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = saveDefaultDocument();

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", document.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<Document> response = client.toBlocking().exchange(request, Document.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(document, response.getBody().get());
    }

    @Test
    void testGetNotFound() {
        MemberProfile admin = createADefaultMemberProfile();
        saveDefaultDocument();
        UUID nonexistentDocumentId = UUID.randomUUID();

        final MutableHttpRequest<Object> request = HttpRequest.GET(String.format("/%s", nonexistentDocumentId))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Document.class));

        assertEquals(HttpStatus.NOT_FOUND, responseException.getStatus());
        assertEquals(String.format("Document with id %s not found", nonexistentDocumentId), responseException.getMessage());
    }

    @Test
    void testUpdateDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = saveDefaultDocument();
        Document updatedDocument = new Document(document.getId(), "Updated Document", "Updated description", "/pdf/updated-document.pdf");

        final HttpRequest<Document> request = HttpRequest.PUT("", updatedDocument)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<Document> response = client.toBlocking().exchange(request, Document.class);

        assertTrue(response.getBody().isPresent());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(updatedDocument, response.getBody().get());
    }

    @Test
    void testUpdateDocumentNotAuthorized() {
        MemberProfile member = createADefaultMemberProfile();
        Document document = saveDefaultDocument();
        Document updatedDocument = new Document(document.getId(), "Updated Document", "Updated description", "/pdf/updated-document.pdf");

        final HttpRequest<Document> request = HttpRequest.PUT("", updatedDocument)
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }

    @Test
    void testUpdateDocumentWithDuplicateName() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = saveDefaultDocument();
        Document newDocument = new Document("Document 2", "A second document", "/pdf/second.pdf");
        getDocumentRepository().save(newDocument);
        newDocument.setName(document.getName());

        final HttpRequest<Document> request = HttpRequest.PUT("", newDocument)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.CONFLICT, responseException.getStatus());
        assertEquals(String.format("A document with the name '%s' already exists", document.getName()), responseException.getMessage());
    }

    @Test
    void testUpdateDocumentWithBlankName() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = saveDefaultDocument();
        document.setName(" ");

        final HttpRequest<Document> request = HttpRequest.PUT("", document)
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.BAD_REQUEST, responseException.getStatus());
        assertEquals("Document name must not be blank", responseException.getMessage());
    }

    @Test
    void testDeleteDocument() {
        MemberProfile admin = createADefaultMemberProfile();
        Document document = saveDefaultDocument();

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", document.getId()))
                .basicAuth(admin.getWorkEmail(), RoleType.Constants.ADMIN_ROLE);
        final HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void testDeleteDocumentNotAuthorized() {
        MemberProfile member = createADefaultMemberProfile();
        Document document = saveDefaultDocument();

        final HttpRequest<?> request = HttpRequest.DELETE(String.format("/%s", document.getId()))
                .basicAuth(member.getWorkEmail(), RoleType.Constants.MEMBER_ROLE);
        final HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("Forbidden", responseException.getMessage());
    }
}
