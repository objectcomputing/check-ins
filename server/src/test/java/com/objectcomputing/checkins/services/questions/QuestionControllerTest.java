package com.objectcomputing.checkins.services.questions;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@MicronautTest
public class QuestionControllerTest {

    @Inject
    @Client("/services/questions")
    private HttpClient client;

    private static final UUID testId = UUID.fromString("12345678-9123-4567-abcd-123456789abc");

    @Inject
    QuestionServices mockQuestionServices;

    @MockBean(QuestionServices.class)
    public QuestionServices getMockQuestionServices() {
        return mock(QuestionServices.class);
    }

    String fakeUuid = "12345678-9123-4567-abcd-123456789abc";

    @BeforeEach
    void setup() {
        reset(mockQuestionServices);
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {
        when(mockQuestionServices.findByQuestionId(testId))
                .thenThrow(new QuestionNotFoundException("fake exception"));
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/"+testId)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertEquals("fake exception", thrown.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testPUTSuccessfulUpdate() {
        Question fakeQuestion = new Question("fake question");
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        when(mockQuestionServices.update(any(Question.class))).thenReturn(fakeQuestion);
        QuestionUpdateDTO requestBody = new QuestionUpdateDTO();
        requestBody.setText(fakeQuestion.getText());
        requestBody.setQuestionId(fakeQuestion.getQuestionid());

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.PUT("/", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("/services/questions/" + fakeQuestion.getQuestionid(),
                response.getHeaders().get("location"));
        assertNotNull(response.getContentLength());
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

        assertEquals("question.questionId: must not be null", responseBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPUTNoQuestionForID() {
        when(mockQuestionServices.update(any(Question.class)))
                .thenThrow(new QuestionBadArgException("fake exception message"));

        QuestionUpdateDTO requestBody = new QuestionUpdateDTO();
        requestBody.setText("Fake Question");
        requestBody.setQuestionId(UUID.fromString(fakeUuid));

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        JsonError responseBody = thrown.getResponse().getBody(JsonError.class).get();

        assertEquals("fake exception message", responseBody.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testPOSTCreateAQuestion() {

        Question fakeQuestion = new Question("fake question");
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));

        when(mockQuestionServices.saveQuestion(any(Question.class))).thenReturn(fakeQuestion);

        QuestionCreateDTO requestBody = new QuestionCreateDTO();
        requestBody.setText(fakeQuestion.getText());
        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.POST("/", requestBody)
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertEquals("/services/questions/" + fakeQuestion.getQuestionid(),
                response.getHeaders().get("location"));
        assertNotNull(response.getContentLength());
    }

    @Test
    public void testPOSTCreateAQuestion_null_question() {

        Question fakeQuestion = new Question("fake question");

        when(mockQuestionServices.saveQuestion(fakeQuestion))
                .thenThrow(new QuestionDuplicateException("fake dupe exception"));

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/", fakeQuestion)
                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        });

        assertEquals("fake dupe exception", thrown.getMessage());
        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());

    }

    @Test
    public void testGETreadAllQuestions() {

        Question fakeQuestion = new Question("fake question text");
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));

        when(mockQuestionServices.readAllQuestions()).thenReturn(Collections.singleton(fakeQuestion));

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.GET("/")
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());
        response.equals(fakeQuestion);
    }

    @Test
    public void testGETFindQuestionsSimilar() {

        Question fakeQuestion = new Question("fake question text");
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));

        when(mockQuestionServices.findByText("fake")).thenReturn(Collections.singleton(fakeQuestion));

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.GET("/?text=fake")
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());
        response.equals(fakeQuestion);
    }

    @Test
    public void testGETGetById_HappyPath() {

        UUID uuid = UUID.fromString(fakeUuid);
        Question fakeQuestion = new Question("Fake question text?");

        fakeQuestion.setQuestionid(uuid);
        List<Question> result = new ArrayList<>();
        result.add(fakeQuestion);

        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        when(mockQuestionServices.findByQuestionId(uuid)).thenReturn(fakeQuestion);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest
                        .GET(String.format("/%s", fakeQuestion.getQuestionid()))
                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(fakeQuestion);
    }

}
