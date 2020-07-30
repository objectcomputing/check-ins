package com.objectcomputing.checkins.services.questions;

import com.objectcomputing.checkins.services.questions.Question;
import com.objectcomputing.checkins.services.questions.QuestionController;
import com.objectcomputing.checkins.services.questions.QuestionRepository;
import com.objectcomputing.checkins.services.questions.QuestionServices;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@MicronautTest
public class QuestionServicesTest {

    private static final Logger LOG = LoggerFactory.getLogger(SkillControllerTest.class);

    @Inject
    @Client("/questions")
    private HttpClient client;

    @Inject
    QuestionServices itemUnderTest;

    QuestionRepository mockQuestionRepository = mock(QuestionRepository.class);
    Question mockQuestion = mock(Question.class);

    String fakeUuid = "12345678-9123-4567-abcd-123456789abc";
    String fakeUuid2 = "22345678-9123-4567-abcd-123456789abc";

    @BeforeEach
    void setup() {
        itemUnderTest.setQuestionRepository(mockQuestionRepository);
        reset(mockQuestionRepository);
        reset(mockQuestion);
    }

    @Test
    public void testSaveQuestion() {

        String fakeQuestion = "this is such a fake question?";
        UUID uuid = UUID.fromString(fakeUuid);
        Question question = new Question();
        question.setQuestionid(uuid);
        question.setText(fakeQuestion);

        when(mockQuestionRepository.save(question)).thenReturn(question);
        Question returned = itemUnderTest.saveQuestion(question);

        assertEquals(question.getQuestionid(), returned.getQuestionid());

    }

    @Test
    public void testSaveQuestion_question_not_found() {

        String fakeQuestionText = "this is such a fake question?";
        Question fakeQuestion = new Question();
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        fakeQuestion.setText(fakeQuestionText);
        Question questionNotThere = new Question();
        questionNotThere.setQuestionid(UUID.fromString(fakeUuid2));
        questionNotThere.setText(fakeQuestionText+"notThere");

        when(mockQuestionRepository.save(fakeQuestion)).thenReturn(fakeQuestion);
        Question returned = itemUnderTest.saveQuestion(fakeQuestion);

        assertEquals(fakeQuestion.getQuestionid(), returned.getQuestionid());

    }

    @Test
    public void testUpdate() {

        String fakeQuestionText = "fake question text";
        Question fakeQuestion = new Question();
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        fakeQuestion.setText(fakeQuestionText);
        Question updatedFakeQuestion = new Question();
        updatedFakeQuestion.setQuestionid(UUID.fromString(fakeUuid2));
        updatedFakeQuestion.setText(fakeQuestionText + "new stuff");

        when(mockQuestionRepository.update(fakeQuestion)).thenReturn(updatedFakeQuestion);
        when(itemUnderTest.findByQuestionId(fakeQuestion.getQuestionid())).thenReturn(updatedFakeQuestion);
        Question returned = itemUnderTest.update(fakeQuestion);

        assertEquals(updatedFakeQuestion.getQuestionid(), returned.getQuestionid());
        assertEquals(updatedFakeQuestion.getText(), returned.getText());

    }
    @Test
    public void testReadAllQuestions() {

        Question fakeQuestion = new Question("this is such a fake question?");
        UUID uuid = UUID.fromString(fakeUuid);
        Question question = new Question();
        question.setQuestionid(uuid);

        List<Question> fakeQuestionList = new ArrayList<>();
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        fakeQuestionList.add(fakeQuestion);

        when(mockQuestionRepository.findAll()).thenReturn(fakeQuestionList);
        List<Question> returned = itemUnderTest.readAllQuestions();

        assertEquals(question.getQuestionid(), returned.get(0).getQuestionid());

    }


    @Test
    public void testFindByQuestionId() {

        Question fakeQuestion = new Question("this is such a fake question?");
        UUID uuid = UUID.fromString(fakeUuid);
        Question question = new Question();
        question.setQuestionid(uuid);

        List<Question> fakeQuestionList = new ArrayList<>();
        fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
        fakeQuestionList.add(fakeQuestion);

        when(mockQuestionRepository.findByQuestionid(uuid)).thenReturn(fakeQuestion);
        Question returned = itemUnderTest.findByQuestionId(uuid);

        assertEquals(question.getQuestionid(), returned.getQuestionid());

    }


}
