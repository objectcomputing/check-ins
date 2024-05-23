package com.objectcomputing.checkins.services.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.QuestionCategoryFixture;
import com.objectcomputing.checkins.services.fixture.QuestionFixture;
import com.objectcomputing.checkins.services.question_category.QuestionCategory;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.JsonError;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuestionControllerTest extends TestContainersSuite implements QuestionFixture, QuestionCategoryFixture {

    @Inject
    @Client("/services/questions")
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
            client.toBlocking().exchange(HttpRequest.GET((String.format("/%s", UUID.randomUUID().toString())))
            .basicAuth(MEMBER_ROLE,MEMBER_ROLE));
        });

    }

    @Test
    void testGETreadAllQuestions() {

        Question question = createADefaultQuestion();
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Question>> response = client.toBlocking().exchange(request, Argument.setOf(Question.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(question);
        assertNotNull(response.getContentLength());

    }

    @Test
    void testGETFindQuestionsSimilar() {

        Question question = createADefaultQuestion();
        String partOfQuestion = question.getText().substring(0,3);
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?text=%s", encodeValue(partOfQuestion))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Question>> response = client.toBlocking().exchange(request, Argument.setOf(Question.class));

        assertNotNull(response.getContentLength());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(question), response.getBody().get());

    }

    @Test
    void testGETGetByIdHappyPath() {

        QuestionCategory questionCategory = createADefaultQuestionCategory();
        Question question = createADefaultQuestionWithCategory(questionCategory.getId());

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", question.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Question> response = client.toBlocking().exchange(request, Question.class);

        assertEquals(question.getCategoryId(), response.body().getCategoryId());
        assertEquals(question.getText(), response.body().getText());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    void testPUTSuccessfulUpdate() {

        Question question = createADefaultQuestion();

        final HttpRequest<Question> request = HttpRequest.
                PUT("/", question).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Question> response = client.toBlocking().exchange(request, Question.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), question.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    void testPUTNoIDSupplied() {

        QuestionCreateDTO requestBody = new QuestionCreateDTO();
        requestBody.setText("Fake Question");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        JsonNode body = thrown.getResponse().getBody(JsonNode.class).orElse(null);
        JsonNode error = Objects.requireNonNull(body).get("_embedded").get("errors").get(0).get("message");
        assertEquals("question.id: must not be null", error.asText());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void testPUTQuestionNotFound() {

        QuestionUpdateDTO requestBody = new QuestionUpdateDTO();
        requestBody.setId(UUID.randomUUID());
        requestBody.setText("Fake Question");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        JsonError responseBody = thrown.getResponse().getBody(JsonError.class).get();

        assertEquals("No question found for this id", responseBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void testPOSTCreateAQuestion() {

        Question question = createADefaultQuestion();
        QuestionCreateDTO newQuestion = new QuestionCreateDTO();
        newQuestion.setText("How do you like working with Mr. Hands?");

        final HttpRequest<QuestionCreateDTO> request = HttpRequest.
                POST("/", newQuestion).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Question> response = client.toBlocking().exchange(request,Question.class);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED,response.getStatus());
        assertEquals(newQuestion.getText(), response.body().getText());
    }

    @Test
    void testPOSTCreateAQuestionAlreadyExists() {

        Question question = createADefaultQuestion();
        QuestionCreateDTO newQuestion = new QuestionCreateDTO();
        newQuestion.setText(question.getText());

        final HttpRequest<QuestionCreateDTO> request = HttpRequest.
                POST("/", newQuestion).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.CONFLICT,responseException.getStatus());

    }

    @Test
    void testPOSTCreateAQuestionNullQuestion() {

        QuestionCreateDTO newQuestion = new QuestionCreateDTO();

        final HttpRequest<QuestionCreateDTO> request = HttpRequest.
                POST("/", newQuestion).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

    @Test
    void testGETFindQuestionWithCategory() {

        QuestionCategory questionCategory = createADefaultQuestionCategory();
        Question question = createADefaultQuestionWithCategory(questionCategory.getId());

        UUID categoryId = question.getCategoryId();
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?categoryId=%s", encodeValue(String.valueOf(categoryId)))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Question>> response = client.toBlocking().exchange(request, Argument.setOf(Question.class));

        assertNotNull(response.getContentLength());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(Set.of(question), response.getBody().get());

    }

    @Test
    void testGETFindQuestionWithCategoryNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?categoryId=%s", encodeValue(String.valueOf(UUID.randomUUID())))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Question>> response = client.toBlocking().exchange(request, Argument.setOf(Question.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        assert(response.body().isEmpty());
    }

}
