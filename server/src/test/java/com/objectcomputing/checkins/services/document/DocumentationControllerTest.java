package com.objectcomputing.checkins.services.document;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.DocumentationFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.role.Role;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.PDL_ROLE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent") // Will throw an exception, which is fine for testing
class DocumentationControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, DocumentationFixture {

    @Inject
    DocumentationClient client;

    @Inject
    @Client("/services/document")
    HttpClient rawClient;

    private Role memberRole;
    private Role pdlRole;
    private Document testDocument;
    private Document twoRolesDocument;
    private Document pdlDocument;
    private Document howToDealWithPdlsDocument;
    private String adminAuth;
    private String memberAuth;
    private BlockingHttpClient http;

    static private String auth(String email, String role) {
        return "Basic " + Base64.getEncoder().encodeToString((email + ":" + role).getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void createTestDocumentation() {
        createAndAssignRoles();
        MemberProfile admin = memberWithoutBoss("admin");
        assignAdminRole(admin);
        adminAuth = auth(admin.getWorkEmail(), ADMIN_ROLE);

        MemberProfile member = memberWithoutBoss("member");
        memberRole = assignMemberRole(member);
        memberAuth = auth(member.getWorkEmail(), MEMBER_ROLE);
        pdlRole = getRoleRepository().findByRole(PDL_ROLE).orElseThrow();

        // And 4 documents
        twoRolesDocument = createDocument("Two roles document", "/two/roles.pdf");
        pdlDocument = createDocument("PDL Document", "/pdl.pdf");
        testDocument = createDocument("Test Document", "https://test.com", "This is a test document");
        howToDealWithPdlsDocument = createDocument("How to deal with PDLs", "/how-to-deal-with-pdls.pdf", "just for members");

        http = rawClient.toBlocking();

        // Assign the member documents so members start with 3 documents
        HttpRequest<?> request = HttpRequest.POST(
            String.format("/%s", memberRole.getId()),
            List.of(testDocument.getId(), twoRolesDocument.getId(),
                    howToDealWithPdlsDocument.getId())
        ).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        http.exchange(request);

        // Assign the pdl documents so pdls start with 2 documents
        request = HttpRequest.POST(
            String.format("/%s", pdlRole.getId()),
            List.of(twoRolesDocument.getId(), pdlDocument.getId())
        ).basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
        http.exchange(request);
    }

    @Test
    void canListDocumentationAsMember() {
        // Alphabetical order
        List<DocumentResponseDTO> orderedExpectation = responseFor(
                howToDealWithPdlsDocument,
                pdlDocument,
                testDocument,
                twoRolesDocument
        );

        List<DocumentResponseDTO> received = client.listAllDocuments(memberAuth);
        assertEquals(orderedExpectation, received);
    }

    @Test
    void canListDocumentationForARoleAsMember() {
        // Order as specified in saveDocumentsToRoles above
        List<DocumentResponseDTO> pdlOrderedExpectation = responseFor(
                twoRolesDocument,
                pdlDocument
        );

        assertEquals(pdlOrderedExpectation, client.listAllDocumentsForRole(memberAuth, pdlRole.getId()));

        // Order as specified in saveDocumentsToRoles above
        List<DocumentResponseDTO> memberOrderedExpectation = responseFor(
                testDocument,
                twoRolesDocument,
                howToDealWithPdlsDocument
        );

        assertEquals(memberOrderedExpectation, client.listAllDocumentsForRole(memberAuth, memberRole.getId()));
    }

    @Test
    void permissionRequiredToCreateDocuments() {
        DocumentCreateDTO newDocument = new DocumentCreateDTO("New Document", "https://new.com", "This is a new document");

        // Member should not be able to create documents
        HttpClientResponseException httpClientResponseException = assertThrows(
                HttpClientResponseException.class,
                () -> client.createDocument(memberAuth, newDocument)
        );
        assertEquals(HttpStatus.FORBIDDEN, httpClientResponseException.getStatus());
    }

    @Test
    void createdDocumentNamesMustBeUnique() {
        DocumentCreateDTO newDocument = new DocumentCreateDTO(testDocument.getName(), "https://new.com", "This is a new document");

        HttpClientResponseException httpClientResponseException = assertThrows(
                HttpClientResponseException.class,
                () -> client.createDocument(adminAuth, newDocument)
        );
        assertEquals(HttpStatus.BAD_REQUEST, httpClientResponseException.getStatus());
        assertEquals("Document named '" + testDocument.getName() + "' already exists", httpClientResponseException.getMessage());
    }

    @Test
    void createdDocumentUrlsMustBeUnique() {
        DocumentCreateDTO newDocument = new DocumentCreateDTO("New document", testDocument.getUrl(), "This is a new document");

        HttpClientResponseException httpClientResponseException = assertThrows(
                HttpClientResponseException.class,
                () -> client.createDocument(adminAuth, newDocument)
        );
        assertEquals(HttpStatus.BAD_REQUEST, httpClientResponseException.getStatus());
        assertEquals("Document with a URL '" + testDocument.getUrl() + "' already exists", httpClientResponseException.getMessage());
    }

    @Test
    void documentsCanBeUpdated() {
        DocumentCreateDTO updatedDocument = new DocumentCreateDTO(testDocument.getName(), "https://new-test.com", "This is an updated test document");
        client.updateDocument(adminAuth, testDocument.getId(), updatedDocument);

        Optional<DocumentResponseDTO> first = client.listAllDocuments(memberAuth).stream().filter(d -> d.getId().equals(testDocument.getId())).findFirst();
        assertEquals(updatedDocument.getName(), first.get().getName());
        assertEquals(updatedDocument.getUrl(), first.get().getUrl());
        assertEquals(updatedDocument.getDescription(), first.get().getDescription());
    }

    @Test
    void documentUpdatesRequirePermission() {
        DocumentCreateDTO updatedDocument = new DocumentCreateDTO(testDocument.getName(), "https://new-test.com", "This is an updated test document");
        UUID testDocumentId = testDocument.getId();

        HttpClientResponseException httpClientResponseException = assertThrows(
                HttpClientResponseException.class,
                () -> client.updateDocument(memberAuth, testDocumentId, updatedDocument)
        );
        assertEquals(HttpStatus.FORBIDDEN, httpClientResponseException.getStatus());
    }

    @Test
    void documentsCanNotBeUpdatedToDuplicateName() {
        DocumentCreateDTO updatedDocument = new DocumentCreateDTO(pdlDocument.getName(), "https://new-test.com", "This is an updated test document");
        UUID testDocumentId = testDocument.getId();

        HttpClientResponseException httpClientResponseException = assertThrows(
                HttpClientResponseException.class,
                () -> client.updateDocument(adminAuth, testDocumentId, updatedDocument)
        );
        assertEquals(HttpStatus.BAD_REQUEST, httpClientResponseException.getStatus());
        assertEquals("Document named '" + pdlDocument.getName() + "' already exists", httpClientResponseException.getMessage());
    }

    @Test
    void documentsCanNotBeUpdatedToDuplicateUrl() {
        DocumentCreateDTO updatedDocument = new DocumentCreateDTO("Fancy rename", pdlDocument.getUrl(), "This is an updated test document");
        UUID testDocumentId = testDocument.getId();

        HttpClientResponseException httpClientResponseException = assertThrows(
                HttpClientResponseException.class,
                () -> client.updateDocument(adminAuth, testDocumentId, updatedDocument)
        );
        assertEquals(HttpStatus.BAD_REQUEST, httpClientResponseException.getStatus());
        assertEquals("Document with a URL '" + pdlDocument.getUrl() + "' already exists", httpClientResponseException.getMessage());
    }

    @Test
    void canCreateDocumentsWithPermission() {
        DocumentCreateDTO newDocument = new DocumentCreateDTO("New Document", "https://new.com", "This is a new document");

        DocumentResponseDTO document = client.createDocument(adminAuth, newDocument);
        assertNotNull(document.getId());
        assertEquals(newDocument.getName(), document.getName());
        assertEquals(newDocument.getUrl(), document.getUrl());
        assertEquals(newDocument.getDescription(), document.getDescription());
    }

    @Test
    void canUpdateDocumentsWithPermission() {
        DocumentCreateDTO newDocument = new DocumentCreateDTO("Updated two roles document", "https://new.com", "This is a new document");

        DocumentResponseDTO document = client.updateDocument(adminAuth, twoRolesDocument.getId(), newDocument);

        // ID remains the same
        assertEquals(twoRolesDocument.getId(), document.getId());

        DocumentResponseDTO updatedTwoRolesDocument = new DocumentResponseDTO(twoRolesDocument.getId(), newDocument.getName(), newDocument.getUrl(), newDocument.getDescription());

        // Then the document is updated in the lists
        // Order as specified in saveDocumentsToRoles above
        List<DocumentResponseDTO> pdlOrderedExpectation = List.of(
                updatedTwoRolesDocument,
                pdlDocument.asResponseDTO()
        );

        assertEquals(pdlOrderedExpectation, client.listAllDocumentsForRole(memberAuth, pdlRole.getId()));

        // Order as specified in saveDocumentsToRoles above
        List<DocumentResponseDTO> memberOrderedExpectation = List.of(
                testDocument.asResponseDTO(),
                updatedTwoRolesDocument,
                howToDealWithPdlsDocument.asResponseDTO()
        );

        assertEquals(memberOrderedExpectation, client.listAllDocumentsForRole(memberAuth, memberRole.getId()));
    }

    @Test
    void deletingAnUnknownDocumentHasNoEffect() {
        assertDoesNotThrow(() -> client.deleteDocument(adminAuth, UUID.randomUUID()));
    }

    @Test
    void deletingAnUnknownDocumentWithoutThePermissionIsForbidden() {
        UUID randomId = UUID.randomUUID();
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.deleteDocument(memberAuth, randomId)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void deletingAnInUseDocumentIsAnError() {
        UUID inUseDocumentId = howToDealWithPdlsDocument.getId();

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.deleteDocument(adminAuth, inUseDocumentId)
        );

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Document is still referenced by a role", e.getMessage());

        // We remove the document from the role it's in
        client.saveDocumentsToRoles(
                adminAuth,
                memberRole.getId(),
                List.of(testDocument.getId(), twoRolesDocument.getId())
        );

        // Now we can delete it
        assertDoesNotThrow(() -> client.deleteDocument(adminAuth, inUseDocumentId));

        // and it's gone
        List<DocumentResponseDTO> orderedExpectation = responseFor(
                pdlDocument,
                testDocument,
                twoRolesDocument
        );
        assertEquals(orderedExpectation, client.listAllDocuments(adminAuth));

        List<DocumentResponseDTO> memberOrderedExpectation = responseFor(
                testDocument,
                twoRolesDocument
        );

        assertEquals(memberOrderedExpectation, client.listAllDocumentsForRole(memberAuth, memberRole.getId()));
    }

    @Test
    void updatingAnUnknownRoleListIsABadRequest() {
        UUID unknownRoleId = UUID.randomUUID();
        List<UUID> list = List.of(testDocument.getId(), twoRolesDocument.getId(), howToDealWithPdlsDocument.getId());

        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.saveDocumentsToRoles(adminAuth, unknownRoleId, list)
        );
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        assertEquals("Role does not exist: " + unknownRoleId, e.getMessage());
    }

    @Test
    void updatingRoleListRequiresThePermission() {
        UUID validRoleId = memberRole.getId();
        List<UUID> list = List.of(testDocument.getId(), twoRolesDocument.getId(), howToDealWithPdlsDocument.getId());
        HttpClientResponseException e = assertThrows(HttpClientResponseException.class, () ->
                client.saveDocumentsToRoles(memberAuth, validRoleId, list)
        );
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

    @Test
    void updatingRoleWithDuplicateDocumentsRemovesDuplicates() {
        client.saveDocumentsToRoles(
                adminAuth,
                memberRole.getId(),
                List.of(testDocument.getId(), testDocument.getId(), testDocument.getId())
        );

        var documents = client.listAllDocumentsForRole(memberAuth, memberRole.getId());
        assertEquals(responseFor(testDocument), documents);
    }

    @Test
    void updatingRoleMaintainsDocumentsOrder() {
        var orderedDocuments = List.of(testDocument, twoRolesDocument, howToDealWithPdlsDocument);

        client.saveDocumentsToRoles(
                adminAuth,
                memberRole.getId(),
                orderedDocuments.stream().map(Document::getId).toList()
        );

        var documents = client.listAllDocumentsForRole(memberAuth, memberRole.getId());
        assertEquals(
                responseFor(orderedDocuments),
                documents
        );

        orderedDocuments = List.of(twoRolesDocument, howToDealWithPdlsDocument, testDocument);

        client.saveDocumentsToRoles(
                adminAuth,
                memberRole.getId(),
                orderedDocuments.stream().map(Document::getId).toList()
        );

        documents = client.listAllDocumentsForRole(memberAuth, memberRole.getId());
        assertEquals(
                responseFor(orderedDocuments),
                documents
        );

        // And the PDL documents are unaffected
        assertEquals(
                responseFor(twoRolesDocument, pdlDocument),
                client.listAllDocumentsForRole(memberAuth, pdlRole.getId())
        );
    }

    @Test
    void updatingARoleListWithUnknownDocumentIdsIsIgnored() {
        var result = client.saveDocumentsToRoles(
                adminAuth,
                memberRole.getId(),
                List.of(
                        testDocument.getId(),
                        UUID.randomUUID(),
                        twoRolesDocument.getId(),
                        UUID.randomUUID(),
                        howToDealWithPdlsDocument.getId()
                )
        );
        assertEquals(
                responseFor(testDocument, twoRolesDocument, howToDealWithPdlsDocument),
                result
        );
    }

    @Test
    void rolesCanBeCleared() {
        client.saveDocumentsToRoles(adminAuth, memberRole.getId(), List.of());
        assertEquals(List.of(), client.listAllDocumentsForRole(memberAuth, memberRole.getId()));
    }

    @Test
    void documentNameIsRequired() {
        String expectedMessage = "document.name: must not be blank";

        var missingNameCreateReq = HttpRequest.POST("/", """
                {"url": "someValue", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(missingNameCreateReq)),
                expectedMessage
        );

        var blankNameCreateReq = HttpRequest.POST("/", """
                {"name": "", "url": "someValue", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(blankNameCreateReq)),
                expectedMessage
        );

        var missingNameUpdateReq = HttpRequest.PUT("/" + testDocument.getId(), """
                {"url": "someValue", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(missingNameUpdateReq)),
                expectedMessage
        );

        var blankNameUpdateReq = HttpRequest.PUT("/" + testDocument.getId(), """
                {"name": "", "url": "someValue", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(blankNameUpdateReq)),
                expectedMessage
        );
    }

    @Test
    void documentUrlIsRequired() {
        String expectedMessage = "document.url: must not be blank";

        var missingUrlCreateReq = HttpRequest.POST("/", """
                {"name": "someValue", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(missingUrlCreateReq)),
                expectedMessage
        );

        var blankUrlCreateReq = HttpRequest.POST("/", """
                {"name": "someValue", "url": "", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(blankUrlCreateReq)),
                expectedMessage
        );

        var missingUrlUpdateReq = HttpRequest.PUT("/" + testDocument.getId(), """
                {"name": "someValue", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(missingUrlUpdateReq)),
                expectedMessage
        );

        var blankUrlUpdateReq = HttpRequest.PUT("/" + testDocument.getId(), """
                {"name": "someValue", "url": "", "description": "someValue"}
                """).header("Authorization", adminAuth);

        assertValidation(
                assertThrows(HttpClientResponseException.class, () -> http.exchange(blankUrlUpdateReq)),
                expectedMessage
        );
    }

    private void assertValidation(HttpClientResponseException e, String expectedMessage) {
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
        String body = e.getResponse().getBody(String.class).get();
        assertTrue(body.contains(expectedMessage), "%s should contain '%s'".formatted(body, expectedMessage));
    }

    private static List<DocumentResponseDTO> responseFor(List<Document> documents) {
        return responseFor(documents.toArray(new Document[0]));
    }

    private static List<DocumentResponseDTO> responseFor(Document... documents) {
        return Arrays.stream(documents).map(Document::asResponseDTO).toList();
    }

    @SuppressWarnings("unused")
    @Client("/services/document")
    private interface DocumentationClient {

        @Get
        List<DocumentResponseDTO> listAllDocuments(@Header String authorization);

        @Get("/{roleId}")
        List<DocumentResponseDTO> listAllDocumentsForRole(@Header String authorization, UUID roleId);

        @Post
        DocumentResponseDTO createDocument(@Header String authorization, @Body DocumentCreateDTO document);

        @Put("/{documentId}")
        DocumentResponseDTO updateDocument(@Header String authorization, UUID documentId, @Body DocumentCreateDTO document);

        @Delete("/{documentId}")
        void deleteDocument(@Header String authorization, UUID documentId);

        @Post("/{roleId}")
        List<DocumentResponseDTO> saveDocumentsToRoles(@Header String authorization, UUID roleId, @Body List<UUID> documentIds);
    }
}
