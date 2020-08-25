// package com.objectcomputing.checkins.services.questions;

// import com.objectcomputing.checkins.services.skills.SkillControllerTest;
// import io.micronaut.http.HttpRequest;
// import io.micronaut.http.client.HttpClient;
// import io.micronaut.http.client.annotation.Client;
// import io.micronaut.http.client.exceptions.HttpClientResponseException;
// import io.micronaut.test.annotation.MicronautTest;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import javax.inject.Inject;
// import java.util.*;

// import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.*;

// @MicronautTest
// public class QuestionServicesImplTest {

//     private static final Logger LOG = LoggerFactory.getLogger(SkillControllerTest.class);

//     @Inject
//     @Client("/services/questions")
//     private HttpClient client;

//     @Inject
//     private QuestionServicesImpl itemUnderTest;


//     QuestionRepository mockQuestionRepository = mock(QuestionRepository.class);
//     Question mockQuestion = mock(Question.class);

//     String fakeUuid = "12345678-9123-4567-abcd-123456789abc";
//     String fakeUuid2 = "22345678-9123-4567-abcd-123456789abc";

//     @BeforeEach
//     void setup() {
//         itemUnderTest.setQuestionRepository(mockQuestionRepository);
//         reset(mockQuestionRepository);
//         reset(mockQuestion);
//     }

//     @Test
//     public void testSaveQuestion() {

//         String fakeQuestion = "this is such a fake question?";
//         UUID uuid = UUID.fromString(fakeUuid);
//         Question question = new Question();
//         question.setQuestionid(uuid);
//         question.setText(fakeQuestion);

//         when(mockQuestionRepository.save(question)).thenReturn(question);
//         Question returned = itemUnderTest.saveQuestion(question);

//         assertEquals(question.getQuestionid(), returned.getQuestionid());

//     }

//     @Test
//     public void testSaveQuestionAlreadyExists() {
//         Question fakeQuestion = new Question("fake question");
//         when(mockQuestionRepository.findAll()).thenReturn(Collections.singleton(fakeQuestion));
//         when(mockQuestionRepository.findByTextIlike("%" + fakeQuestion.getText() + "%"))
//                 .thenReturn(Set.of(fakeQuestion));

//         QuestionDuplicateException thrown = assertThrows(QuestionDuplicateException.class, () -> {
//             itemUnderTest.saveQuestion(fakeQuestion);
//         });

//         assertEquals("Already exists", thrown.getMessage());
//     }

//     @Test
//     public void testSaveQuestion_question_not_found() {

//         String fakeQuestionText = "this is such a fake question?";
//         Question fakeQuestion = new Question();
//         fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
//         fakeQuestion.setText(fakeQuestionText);
//         Question questionNotThere = new Question();
//         questionNotThere.setQuestionid(UUID.fromString(fakeUuid2));
//         questionNotThere.setText(fakeQuestionText+"notThere");

//         when(mockQuestionRepository.save(fakeQuestion)).thenReturn(fakeQuestion);
//         Question returned = itemUnderTest.saveQuestion(fakeQuestion);

//         assertEquals(fakeQuestion.getQuestionid(), returned.getQuestionid());

//     }

//     @Test
//     public void testUpdate() {

//         String fakeQuestionText = "fake question text";
//         Question fakeQuestion = new Question();
//         fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
//         fakeQuestion.setText(fakeQuestionText);
//         Question updatedFakeQuestion = new Question();
//         updatedFakeQuestion.setQuestionid(UUID.fromString(fakeUuid2));
//         updatedFakeQuestion.setText(fakeQuestionText + "new stuff");

//         when(mockQuestionRepository.update(fakeQuestion))
//                 .thenReturn(updatedFakeQuestion);
//         when(mockQuestionRepository.findByQuestionid(fakeQuestion.getQuestionid()))
//                 .thenReturn(Optional.of(updatedFakeQuestion));
//         Question returned = itemUnderTest.update(fakeQuestion);

//         assertEquals(updatedFakeQuestion.getQuestionid(), returned.getQuestionid());
//         assertEquals(updatedFakeQuestion.getText(), returned.getText());

//     }

//     @Test
//     public void testUpdateNonexistentRecord() {
//         Question fakeQuestion = new Question("fake question");
//         fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
//         when(mockQuestionRepository.findByQuestionid(fakeQuestion.getQuestionid()))
//                 .thenReturn(Optional.empty());

//         QuestionBadArgException thrown = assertThrows(QuestionBadArgException.class, () -> {
//             itemUnderTest.update(fakeQuestion);
//         });

//         assertEquals("No question found for this uuid", thrown.getMessage());
//     }

//     @Test
//     public void testReadAllQuestions() {

//         Question fakeQuestion = new Question("this is such a fake question?");
//         UUID uuid = UUID.fromString(fakeUuid);
//         Question question = new Question();
//         question.setQuestionid(uuid);

//         Set<Question> fakeQuestionList = new HashSet<>();
//         fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
//         fakeQuestionList.add(fakeQuestion);

//         when(mockQuestionRepository.findAll()).thenReturn(fakeQuestionList);
//         Set<Question> returned = itemUnderTest.readAllQuestions();

//         assertEquals(1, returned.size());
//         assertEquals(question.getQuestionid(), returned.iterator().next().getQuestionid());

//     }


//     @Test
//     public void testFindByQuestionId() {

//         Question fakeQuestion = new Question("this is such a fake question?");
//         UUID uuid = UUID.fromString(fakeUuid);
//         Question question = new Question();
//         question.setQuestionid(uuid);

//         List<Question> fakeQuestionList = new ArrayList<>();
//         fakeQuestion.setQuestionid(UUID.fromString(fakeUuid));
//         fakeQuestionList.add(fakeQuestion);

//         when(mockQuestionRepository.findByQuestionid(uuid)).thenReturn(Optional.of(fakeQuestion));
//         Question returned = itemUnderTest.findByQuestionId(uuid);

//         assertEquals(question.getQuestionid(), returned.getQuestionid());

//     }


// }
