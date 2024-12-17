package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.kudos.Kudos;
import com.objectcomputing.checkins.services.TestContainersSuite;
import com.objectcomputing.checkins.services.fixture.KudosFixture;
import com.objectcomputing.checkins.services.fixture.RoleFixture;
import com.objectcomputing.checkins.services.fixture.MemberProfileFixture;
import com.objectcomputing.checkins.services.fixture.EmployeeHoursFixture;
import com.objectcomputing.checkins.services.fixture.ReviewPeriodFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackTemplateFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackRequestFixture;
import com.objectcomputing.checkins.services.fixture.TemplateQuestionFixture;
import com.objectcomputing.checkins.services.fixture.FeedbackAnswerFixture;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHours;

import io.micronaut.core.util.StringUtils;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.multipart.MultipartBody;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.objectcomputing.checkins.services.role.RoleType.Constants.ADMIN_ROLE;
import static com.objectcomputing.checkins.services.role.RoleType.Constants.MEMBER_ROLE;
import static io.micronaut.http.MediaType.MULTIPART_FORM_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "replace.fileservicesimpl", value = StringUtils.TRUE)
class ReportDataControllerTest extends TestContainersSuite implements MemberProfileFixture, RoleFixture, KudosFixture, ReviewPeriodFixture, FeedbackTemplateFixture, FeedbackRequestFixture, TemplateQuestionFixture, FeedbackAnswerFixture, EmployeeHoursFixture {

  @Inject
  @Client("/services/report/data")
  HttpClient client;

  @Inject
  private FileServicesImplReplacement fileServices;

  private EmployeeHours employeeHours;
  private FeedbackTemplate feedbackTemplate;
  private ReviewPeriod reviewPeriod;
  private FeedbackRequest feedbackRequest;
  private TemplateQuestion questionOne;
  private TemplateQuestion questionTwo;
  private MemberProfile regular;
  private MemberProfile admin;
  private Kudos kudos;
  private final String basePath = "src/test/java/com/objectcomputing/checkins/services/reports/";

  @BeforeEach
  void createRolesAndPermissions() {
    createAndAssignRoles();
    regular = createADefaultMemberProfile();
    admin = createAThirdDefaultMemberProfile();
    assignAdminRole(admin);

    feedbackTemplate = saveFeedbackTemplate(admin.getId());
    reviewPeriod = createAClosedReviewPeriod(
                       LocalDate.now().minusDays(30).atStartOfDay(),
                       LocalDate.now().plusDays(1).atStartOfDay());
    questionOne = saveTemplateQuestion(feedbackTemplate, 1);
    questionTwo = saveAnotherTemplateQuestion(feedbackTemplate, 2);
    feedbackRequest = saveSampleFeedbackRequest(admin, regular, admin,
                                                feedbackTemplate.getId(),
                                                reviewPeriod, "submitted");
    saveSampleFeedbackAnswer(questionOne.getId(), feedbackRequest.getId());
    saveSampleFeedbackAnswer(questionTwo.getId(), feedbackRequest.getId());

    kudos = createApprovedKudos(admin.getId());
    createKudosRecipient(kudos.getId(), regular.getId());

    employeeHours = new EmployeeHours(regular.getEmployeeId(),
                                      Float.valueOf(1413), Float.valueOf(1371),
                                      Float.valueOf(0), LocalDate.now(),
                                      Float.valueOf(1850), LocalDate.now(),
                                      Float.valueOf(90), Float.valueOf(10));
    saveEmployeeHours(employeeHours);
  }

  @Test
  void uploadReportData() {
    final HttpRequest<?> request = postData(admin, ADMIN_ROLE);
    final String response = client.toBlocking().retrieve(request);
    assertNotNull(response);
  }

  @Test
  void uploadReportDataWithoutPermission() {
    final HttpRequest<?> request = postData(regular, MEMBER_ROLE);
    HttpClientResponseException responseException =
      assertThrows(HttpClientResponseException.class,
                   () -> client.toBlocking().retrieve(request));
  }

  @Test
  void processReportData() {
    MemberProfile target = regular;
    HttpRequest<?> request = postData(admin, ADMIN_ROLE);
    final String response = client.toBlocking().retrieve(request);
    assertNotNull(response);

    request = HttpRequest.GET(
          String.format("/generate?memberIds=%s&reviewPeriodId=%s",
                        target.getId(),
                        reviewPeriod.getId().toString()))
                .basicAuth(admin.getWorkEmail(), ADMIN_ROLE);
    client.toBlocking().exchange(request);

    validateReportData(fileServices.documentName,
                       fileServices.documentText, target);
  }

  @Test
  void processReportDataWithoutPermission() {
    final HttpRequest<?> request = HttpRequest.GET(
          String.format("/generate?memberIds=%s&reviewPeriodId=%s",
                        regular.getId(),
                        reviewPeriod.getId()))
                .basicAuth(regular.getWorkEmail(), MEMBER_ROLE);
    HttpClientResponseException responseException =
      assertThrows(HttpClientResponseException.class,
                   () -> client.toBlocking().retrieve(request));
  }

  HttpRequest<?> postData(MemberProfile user, String role) {
    File compFile = new File(basePath + "data/compensationHistory.csv");
    File currFile = new File(basePath + "data/currentInformation.csv");
    File posFile  = new File(basePath + "data/positionHistory.csv");

    return HttpRequest.POST("/upload",
                MultipartBody.builder()
                .addPart("comp", compFile)
                .addPart("curr", currFile)
                .addPart("pos", posFile)
                .build())
                .basicAuth(user.getWorkEmail(), role)
                .contentType(MULTIPART_FORM_DATA);
  }

  private String formatDate(LocalDate date) {
      return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
  }

  void validateReportData(String filename, String text, MemberProfile user) {
    assertEquals(user.getWorkEmail(), filename);

    // Review Period
    assertTrue(text.contains(
        formatDate(reviewPeriod.getPeriodStartDate().toLocalDate())));
    assertTrue(text.contains(
        formatDate(reviewPeriod.getPeriodEndDate().toLocalDate())));

    // Member Info
    assertTrue(text.contains(user.getFirstName()));
    assertTrue(text.contains(user.getLastName()));
    assertTrue(text.contains(user.getTitle()));

    // Kudos
    assertTrue(text.contains(kudos.getMessage()));

    // Feedback
    assertTrue(text.contains(feedbackTemplate.getTitle()));
    assertTrue(text.contains(questionOne.getQuestion()));
    assertTrue(text.contains(questionTwo.getQuestion()));
    assertTrue(text.contains(
        formatDate(feedbackRequest.getSubmitDate())));

    // Hours
    final String format = "%.2f";
    assertTrue(text.contains(
        String.format(format, employeeHours.getContributionHours())));
    assertTrue(text.contains(
        String.format(format, employeeHours.getPtoHours())));
    assertTrue(text.contains(
        String.format(format, employeeHours.getOvertimeWorked())));
    assertTrue(text.contains(
        String.format(format, employeeHours.getBillableUtilization())));
  }
}
