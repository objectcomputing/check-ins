package com.objectcomputing.checkins.services.question_category;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.QuestionCategoryFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.JsonError;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuestionCategoryControllerTest extends TestContainersSuite implements QuestionCategoryFixture, MemberProfileFixture, RoleFixture {

    @Inject
    @Client("/services/question-categories")
    private HttpClient client;

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Test
    void testGETNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET(String.format("/?id=%s", UUID.randomUUID().toString()))
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testGETFindByNameReturnsNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?name=%s", encodeValue("silly"))).basicAuth(MEMBER_ROLE, MEMBER_ROLE);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
                    final HttpResponse<Set<QuestionCategory>> response = client.toBlocking().exchange(request, Argument.setOf(QuestionCategory.class));
                });
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());

    }

    @Test
    void testGetAllCategories() {
        QuestionCategory questionCategory = createADefaultQuestionCategory();
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<QuestionCategory>> response = client.toBlocking().exchange(request, Argument.setOf(QuestionCategory.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(questionCategory);
        assertNotNull(response.getContentLength());

    }

    @Test
    void testGETGetByIdHappyPath() {

        QuestionCategory questionCategory = createADefaultQuestionCategory();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?id=%s", questionCategory.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<QuestionCategory> response = client.toBlocking().exchange(request, QuestionCategory.class);

        assertEquals(questionCategory.getName(), response.body().getName());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?id=%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    void testPUTSuccessfulUpdate() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        QuestionCategory questionCategory = createADefaultQuestionCategory();

        final HttpRequest<QuestionCategory> request = HttpRequest.
                PUT("/", questionCategory).basicAuth(memberProfileOfAdmin.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<QuestionCategory> response = client.toBlocking().exchange(request, QuestionCategory.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), questionCategory.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    void testPUTUpdateNoPermission() {

        QuestionCategory questionCategory = createADefaultQuestionCategory();

        final HttpRequest<QuestionCategory> request = HttpRequest.
                PUT("/", questionCategory).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("You do not have permission to access this resource", responseException.getMessage());

    }

    @Test
    void testPUTNoIDSupplied() {

        QuestionCategoryCreateDTO requestBody = new QuestionCategoryCreateDTO();
        requestBody.setName("Fake Category");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
                    .basicAuth(ADMIN_ROLE, ADMIN_ROLE));
        });

        JsonError responseBody = thrown.getResponse().getBody(JsonError.class).get();

        assertEquals("This question category does not exist", responseBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void testPUTQuestionCategoryNotFound() {

        QuestionCategory requestBody = new QuestionCategory();
        requestBody.setId(UUID.randomUUID());
        requestBody.setName("Fake Category");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        JsonError responseBody = thrown.getResponse().getBody(JsonError.class).get();

        assertEquals(("No category with id " + requestBody.getId()), responseBody.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    void testPOSTCreateAQuestionCategory() {

        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        QuestionCategoryCreateDTO newQuestionCategory = new QuestionCategoryCreateDTO();
        newQuestionCategory.setName("Inquisitive");

        final HttpRequest<QuestionCategoryCreateDTO> request = HttpRequest.
                POST("/", newQuestionCategory).basicAuth(memberProfileOfAdmin.getWorkEmail(),ADMIN_ROLE);
        final HttpResponse<QuestionCategory> response = client.toBlocking().exchange(request,QuestionCategory.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED,response.getStatus());
        assertEquals(newQuestionCategory.getName(), response.body().getName());
    }

    @Test
    void testPOSTCreateAQuestionCategoryNoPermission() {

        QuestionCategoryCreateDTO newQuestionCategory = new QuestionCategoryCreateDTO();
        newQuestionCategory.setName("Inquisitive");

        final HttpRequest<QuestionCategoryCreateDTO> request = HttpRequest.
                POST("/", newQuestionCategory).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals(HttpStatus.FORBIDDEN, responseException.getStatus());
        assertEquals("You do not have permission to access this resource", responseException.getMessage());
    }

    @Test
    void testPOSTCreateAQuestionCategoryAlreadyExists() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        QuestionCategory questionCategory = createADefaultQuestionCategory();
        QuestionCategoryCreateDTO newQuestionCategory = new QuestionCategoryCreateDTO();
        newQuestionCategory.setName(questionCategory.getName());

        final HttpRequest<QuestionCategoryCreateDTO> request = HttpRequest.
                POST("/", newQuestionCategory).basicAuth(memberProfileOfAdmin.getWorkEmail(), ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT,responseException.getStatus());

    }

    @Test
    void testPOSTCreateAQuestionNullQuestion() {

        QuestionCategoryCreateDTO newQuestionCategory = new QuestionCategoryCreateDTO();

        final HttpRequest<QuestionCategoryCreateDTO> request = HttpRequest.
                POST("/", newQuestionCategory).basicAuth(ADMIN_ROLE, ADMIN_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    void testDELETEQuestionCategory() {
        MemberProfile memberProfileOfAdmin = createAnUnrelatedUser();
        createAndAssignAdminRole(memberProfileOfAdmin);

        QuestionCategory questionCategory = createADefaultQuestionCategory();
        QuestionCategoryCreateDTO newQuestionCategory = new QuestionCategoryCreateDTO();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", questionCategory.getId())).basicAuth(memberProfileOfAdmin.getWorkEmail(),ADMIN_ROLE);

        final HttpResponse<Boolean> response = client.toBlocking().exchange(request, Boolean.class);

        assertEquals(HttpStatus.OK,response.getStatus());
    }

    @Test
    void testDELETEQuestionCategoryNoPermission() {

        QuestionCategory questionCategory = createADefaultQuestionCategory();

        final HttpRequest<Object> request = HttpRequest.
                DELETE(String.format("/%s", questionCategory.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertEquals("You do not have permission to access this resource", responseException.getMessage());
        assertEquals(HttpStatus.FORBIDDEN,responseException.getStatus());

    }

}
