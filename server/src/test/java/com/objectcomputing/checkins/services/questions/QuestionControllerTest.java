package com.objectcomputing.checkins.services.questions;

import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.QuestionFixture;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.JsonError;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuestionControllerTest extends TestContainersSuite implements QuestionFixture {

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
    public void testGETNonExistingEndpointReturns404() {

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc")
            .basicAuth(MEMBER_ROLE,MEMBER_ROLE));
        });

    }

    @Test
    public void testGETreadAllQuestions() {

        Question question = createADefaultQuestion();
        final HttpRequest<Object> request = HttpRequest.
                GET("/").basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Question>> response = client.toBlocking().exchange(request, Argument.setOf(Question.class));

        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(question);
        assertNotNull(response.getContentLength());

    }

    @Test
    public void testGETFindQuestionsSimilar() {

        Question question = createADefaultQuestion();
        String partOfQuestion = question.getText().substring(0,3);
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/?text=%s", encodeValue(String.valueOf(partOfQuestion)))).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Set<Question>> response = client.toBlocking().exchange(request, Argument.setOf(Question.class));

        assertNotNull(response.getContentLength());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());

    }

    @Test
    public void testGETGetByIdHappyPath() {

        Question question = createADefaultQuestion();

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", question.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Question> response = client.toBlocking().exchange(request, Question.class);

        assertEquals(question.getText(), response.body().getText());
        assertEquals(HttpStatus.OK,response.getStatus());

    }

    @Test
    public void testGETGetByIdNotFound() {

        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", UUID.randomUUID().toString())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.NOT_FOUND,responseException.getStatus());

    }

    @Test
    public void testPUTSuccessfulUpdate() {

        Question question = createADefaultQuestion();

        final HttpRequest<Question> request = HttpRequest.
                PUT("/", question).basicAuth(MEMBER_ROLE,MEMBER_ROLE);
        final HttpResponse<Question> response = client.toBlocking().exchange(request, Question.class);

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals(String.format("%s/%s", request.getPath(), question.getId()),
                response.getHeaders().get("location"));
    }

    @Test
    public void testPUTNoIDSupplied() {

        QuestionCreateDTO requestBody = new QuestionCreateDTO();
        requestBody.setText("Fake Question");

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        JsonError responseBody = thrown.getResponse().getBody(JsonError.class).get();

        assertEquals("question.id: must not be null", responseBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPUTQuestionNotFound() {

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
    public void testPOSTCreateAQuestion() {

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
    public void testPOSTCreateAQuestionAlreadyExists() {

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
    public void testPOSTCreateAQuestionNullQuestion() {

        QuestionCreateDTO newQuestion = new QuestionCreateDTO();

        final HttpRequest<QuestionCreateDTO> request = HttpRequest.
                POST("/", newQuestion).basicAuth(MEMBER_ROLE, MEMBER_ROLE);
        HttpClientResponseException responseException = assertThrows(HttpClientResponseException.class,
                () -> client.toBlocking().exchange(request, Map.class));

        assertNotNull(responseException.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST,responseException.getStatus());

    }

}
