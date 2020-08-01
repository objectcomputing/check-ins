package com.objectcomputing.checkins.services.questions;

import com.objectcomputing.checkins.services.skills.SkillControllerTest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MicronautTest
public class QuestionControllerTest {

    @Inject
    @Client("/services/questions")
    private HttpClient client;

    @Inject
    QuestionController itemUnderTest;

    QuestionServices mockQuestionServices = mock(QuestionServices.class);
    Question mockQuestion = mock(Question.class);

    String fakeUuid = "12345678-9123-4567-abcd-123456789abc";

    @BeforeEach
    void setup() {
        itemUnderTest.setQuestionService(mockQuestionServices);
        reset(mockQuestionServices);
        reset(mockQuestion);
    }

    @Test
    public void testGETNonExistingEndpointReturns404() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.GET("/12345678-9123-4567-abcd-123456789abc"));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testPOSTCreateAQuestion() {

        Question fakeQuestion = new Question("fake question");

        when(mockQuestionServices.saveQuestion(fakeQuestion)).thenReturn(fakeQuestion);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.POST("/", fakeQuestion));

        assertEquals(HttpStatus.CREATED, response.getStatus());
        assertNotNull(response.getContentLength());
    }

    @Test
    public void testPOSTCreateAQuestion_null_question() {

        Question fakeQuestion = new Question("fake question");

        when(mockQuestionServices.saveQuestion(fakeQuestion)).thenReturn(null);

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/", fakeQuestion));
        });

        assertNotNull(thrown.getResponse());
        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());

    }

    @Test
    public void testGETreadAllQuestions() {

        Question fakeQuestion = new Question("fake question text");
        List<Question> fakeQuestionList = new ArrayList<>();
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        fakeQuestionList.add(fakeQuestion);

        when(mockQuestionServices.readAllQuestions()).thenReturn(fakeQuestionList);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.GET("/"));

        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getContentLength());
        response.equals(fakeQuestion);
    }

    @Test
    public void testGETFindQuestionsSimilar() {

        Question fakeQuestion = new Question("fake question text");
        List<Question> fakeQuestionList = new ArrayList<>();
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        fakeQuestionList.add(fakeQuestion);

        when(mockQuestionServices.findByText("fake")).thenReturn(fakeQuestionList);

        final HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.GET("/?text=fake"));

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
                        .GET(String.format("/%s", fakeQuestion.getQuestionid())));
        assertEquals(HttpStatus.OK, response.getStatus());
        response.equals(fakeQuestion);
    }

}
