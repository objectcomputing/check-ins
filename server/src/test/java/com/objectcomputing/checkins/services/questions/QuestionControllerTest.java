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
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
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
        System.out.println("question id =      " + question.getId());
        final HttpRequest<Object> request = HttpRequest.
                GET(String.format("/%s", question.getId())).basicAuth(MEMBER_ROLE,MEMBER_ROLE);

        final HttpResponse<Question> response = client.toBlocking().exchange(request, Question.class);

  //      assertEquals(question, response.body());
        assertEquals(HttpStatus.OK,response.getStatus());

//        UUID uuid = UUID.fromString(fakeUuid);
//        Question fakeQuestion = new Question("Fake question text?");
//
//        fakeQuestion.setId(uuid);
//        List<Question> result = new ArrayList<>();
//        result.add(fakeQuestion);
//
//        fakeQuestion.setId(UUID.fromString(fakeUuid));
//        when(mockQuestionServices.findById(uuid)).thenReturn(fakeQuestion);
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest
//                        .GET(String.format("/%s", fakeQuestion.getId()))
//                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
//        assertEquals(HttpStatus.OK, response.getStatus());
//        response.equals(fakeQuestion);
    }

//    @Test
//    public void testPUTSuccessfulUpdate() {
//        Question fakeQuestion = new Question("fake question");
//        fakeQuestion.setId(UUID.fromString(fakeUuid));
//        when(mockQuestionServices.update(any(Question.class))).thenReturn(fakeQuestion);
//        QuestionUpdateDTO requestBody = new QuestionUpdateDTO();
//        requestBody.setText(fakeQuestion.getText());
//        requestBody.setId(fakeQuestion.getId());
//
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest.PUT("/", requestBody)
//                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
//        assertEquals(HttpStatus.OK, response.getStatus());
//        assertEquals("/services/questions/" + fakeQuestion.getId(),
//                response.getHeaders().get("location"));
//        assertNotNull(response.getContentLength());
//    }

//    @Test
//    public void testPUTNoIDSupplied() {
//        QuestionCreateDTO requestBody = new QuestionCreateDTO();
//        requestBody.setText("Fake Question");
//
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
//                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
//        });
//
//        JsonError responseBody = thrown.getResponse().getBody(JsonError.class).get();
//
//        assertEquals("question.id: must not be null", responseBody.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//    }
//
//    @Test
//    public void testPUTNoQuestionForID() {
//        when(mockQuestionServices.update(any(Question.class)))
//                .thenThrow(new QuestionBadArgException("fake exception message"));
//
//        QuestionUpdateDTO requestBody = new QuestionUpdateDTO();
//        requestBody.setText("Fake Question");
//        requestBody.setId(UUID.fromString(fakeUuid));
//
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.PUT("/", requestBody)
//                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
//        });
//
//        JsonError responseBody = thrown.getResponse().getBody(JsonError.class).get();
//
//        assertEquals("fake exception message", responseBody.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
//    }

    @Test
    public void testPOSTCreateAQuestion() {

//        Question fakeQuestion = new Question("fake question");
//        fakeQuestion.setId(UUID.fromString(fakeUuid));
//
//        when(mockQuestionServices.saveQuestion(any(Question.class))).thenReturn(fakeQuestion);
//
//        QuestionCreateDTO requestBody = new QuestionCreateDTO();
//        requestBody.setText(fakeQuestion.getText());
//        final HttpResponse<?> response = client.toBlocking()
//                .exchange(HttpRequest.POST("/", requestBody)
//                        .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
//
//        assertEquals(HttpStatus.CREATED, response.getStatus());
//        assertEquals("/services/questions/" + fakeQuestion.getId(),
//                response.getHeaders().get("location"));
//        assertNotNull(response.getContentLength());
    }

//    @Test
//    public void testPOSTCreateAQuestionNullQuestion() {
//
//        Question fakeQuestion = new Question("fake question");
//
//        when(mockQuestionServices.saveQuestion(fakeQuestion))
//                .thenThrow(new QuestionDuplicateException("fake dupe exception"));
//
//        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () -> {
//            client.toBlocking().exchange(HttpRequest.POST("/", fakeQuestion)
//                    .basicAuth(MEMBER_ROLE, MEMBER_ROLE));
//        });
//
//        assertEquals("fake dupe exception", thrown.getMessage());
//        assertEquals(HttpStatus.CONFLICT, thrown.getStatus());
//
//    }

}
