package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.exceptions.NotFoundException;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipient;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.kudos.Kudos;

import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriod;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequest;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerServices;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswer;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.Month;
import java.nio.ByteBuffer;
import java.io.IOException;

public class ReportDataCollation {
    private static final Logger LOG = LoggerFactory.getLogger(ReportDataCollation.class);

    private class LocalDateRange {
        public LocalDate start;
        public LocalDate end;
        public LocalDateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
    }

    private enum FeedbackType {
      selfReviews, reviews, feedback
    }

    private static final String textQuestion = "TEXT";
    private static final String radioQuestion = "RADIO";
    private UUID memberId;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID reviewPeriodId;
    private CompensationHistory compensationHistory;
    private CurrentInformation currentInformation;
    private PositionHistory positionHistory;
    private KudosRepository kudosRepository;
    private KudosRecipientRepository kudosRecipientRepository;
    private MemberProfileRepository memberProfileRepository;
    private ReviewPeriodServices reviewPeriodServices;
    private ReportDataServices reportDataServices;
    private FeedbackTemplateServices feedbackTemplateServices;
    private FeedbackRequestServices feedbackRequestServices;
    private FeedbackAnswerServices feedbackAnswerServices;
    private TemplateQuestionServices templateQuestionServices;

    public ReportDataCollation(
                          UUID memberId, UUID reviewPeriodId,
                          KudosRepository kudosRepository,
                          KudosRecipientRepository kudosRecipientRepository,
                          MemberProfileRepository memberProfileRepository,
                          ReviewPeriodServices reviewPeriodServices,
                          ReportDataServices reportDataServices,
                          FeedbackTemplateServices feedbackTemplateServices,
                          FeedbackRequestServices feedbackRequestServices,
                          FeedbackAnswerServices feedbackAnswerServices,
                          TemplateQuestionServices templateQuestionServices) {
        this.memberId = memberId;
        this.reviewPeriodId = reviewPeriodId;
        this.compensationHistory = new CompensationHistory();
        this.currentInformation = new CurrentInformation();
        this.positionHistory = new PositionHistory();
        this.kudosRepository = kudosRepository;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.memberProfileRepository = memberProfileRepository;
        this.reviewPeriodServices = reviewPeriodServices;
        this.reportDataServices = reportDataServices;
        this.feedbackTemplateServices = feedbackTemplateServices;
        this.feedbackRequestServices = feedbackRequestServices;
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.templateQuestionServices = templateQuestionServices;
        LocalDateRange range = getDateRange();
        startDate = range.start;
        endDate = range.end;
    }

    LocalDate getStartDate() {
        return startDate;
    }

    LocalDate getEndDate() {
        return endDate;
    }

    /// Get the kudos given to the member during the start and end date range.
    public List<ReportKudos> getKudos() {
        List<KudosRecipient> recipients = kudosRecipientRepository.findByMemberId(memberId);
        List<ReportKudos> kudosList = new ArrayList<ReportKudos>();
        for (KudosRecipient recipient : recipients) {
            Kudos kudos = kudosRepository.findById(recipient.getKudosId())
                                         .orElse(null);
            if (kudos != null) {
                LocalDate created = kudos.getDateCreated();
                if ((created.isEqual(startDate) ||
                     created.isAfter(startDate)) && created.isBefore(endDate)) {
                    MemberProfile senderProfile =
                      memberProfileRepository.findById(kudos.getSenderId()).orElse(null);
                    String sender = senderProfile == null ?
                              "Unknown" :
                              MemberProfileUtils.getFullName(senderProfile);
                    kudosList.add(new ReportKudos(created,
                                                  kudos.getMessage(), sender));
                }
            }
        }
        return kudosList;
    }

    /// Get the member name, title, and start date among others.
    public MemberProfile getMemberProfile() {
        return memberProfileRepository.findById(memberId).orElseThrow(() ->
            new NotFoundException("Member not found")
        );
    }

    public List<CompensationHistory.Compensation> getCompensationHistory() {
        try {
            ByteBuffer buffer = reportDataServices.get(
                    ReportDataServices.DataType.compensationHistory);
            compensationHistory.load(memberProfileRepository, buffer);
        } catch(IOException ex) {
        }
        return compensationHistory.getHistory(memberId);
    }

    public CurrentInformation.Information getCurrentInformation() {
        try {
            ByteBuffer buffer = reportDataServices.get(
                    ReportDataServices.DataType.currentInformation);
            currentInformation.load(memberProfileRepository, buffer);
        } catch(IOException ex) {
        }
        return currentInformation.getInformation(memberId);
    }

    public List<PositionHistory.Position> getPositionHistory() {
        try {
            ByteBuffer buffer = reportDataServices.get(
                    ReportDataServices.DataType.positionHistory);
            positionHistory.load(memberProfileRepository, buffer);
        } catch(IOException ex) {
        }
        return positionHistory.getHistory(memberId);
    }

    public List<Feedback> getSelfReviews() {
      return getFeedbackType(FeedbackType.selfReviews);
    }

    public List<Feedback> getReviews() {
      return getFeedbackType(FeedbackType.reviews);
    }

    public List<Feedback> getFeedback() {
      return getFeedbackType(FeedbackType.feedback);
    }

    private List<Feedback> getFeedbackType(FeedbackType type) {
      List<Feedback> feedback = new ArrayList<Feedback>();

      // Get the list of requests for the member and review period.
      // We will need to cross-reference the templates.
      LocalDateRange dateRange = getDateRange();
      List<FeedbackRequest> requests =
        feedbackRequestServices.findByValues(null, memberId, null,
                                             dateRange.start,
                                             null, null, null);

      // Iterate over each request and find the template.  Determine the purpose
      // of the template.
      ReviewPeriod reviewPeriod = reviewPeriodServices.findById(reviewPeriodId);
      Map<UUID, String> templates = new HashMap<UUID, String>();
      for (FeedbackRequest request: requests) {
        if (request.getReviewPeriodId() == null &&
            !templates.containsKey(request.getTemplateId())) {
          try {
            FeedbackTemplate template =
                   feedbackTemplateServices.getById(request.getTemplateId());
            boolean use = true;
            switch(type) {
              case FeedbackType.selfReviews:
                use = template.getIsReview() &&
                      template.getId().equals(
                        reviewPeriod.getSelfReviewTemplateId());
                break;
              case FeedbackType.reviews:
                use = template.getIsReview() &&
                      template.getId().equals(
                        reviewPeriod.getReviewTemplateId());
                break;
              case FeedbackType.feedback:
                use = !template.getIsReview();
                break;
            }
            if (use) {
              templates.put(template.getId(), template.getTitle());
            }
          } catch(NotFoundException ex) {
            LOG.error(ex.toString());
          }
        }
      }

      // Go through each template, find the request that corresponds to the
      // template, find the question and answers and put it all together.
      for (UUID templateId : templates.keySet()) {
        String templateTitle = templates.get(templateId);
        List<Feedback.Answer> feedbackAnswers =
                                   new ArrayList<Feedback.Answer>();
        for (FeedbackRequest request: requests) {
          if (request.getTemplateId().equals(templateId)) {
            UUID recipientId = request.getRecipientId();
            MemberProfile recipient = memberProfileRepository.findById(
                                        recipientId).orElse(null);
            String recipientName = (recipient == null ?
                recipientId.toString() :
                MemberProfileUtils.getFullName(recipient));
            List<FeedbackAnswer> answers =
                   feedbackAnswerServices.findByValues(null, request.getId());
            for (FeedbackAnswer answer : answers) {
              String questionText;
              String questionType = textQuestion;
              int questionNumber = 0;
              try {
                TemplateQuestion question =
                    templateQuestionServices.getById(answer.getQuestionId());
                questionText = question.getQuestion();
                questionType = question.getInputType();
                questionNumber = question.getQuestionNumber();
              } catch(NotFoundException ex) {
                LOG.error(ex.toString());
                questionText = answer.getQuestionId().toString();
              }

              feedbackAnswers.add(
                  new Feedback.Answer(
                        recipientName, request.getSubmitDate(), questionText,
                        questionType.equals(textQuestion) ||
                        questionType.equals(radioQuestion) ?
                               answer.getAnswer() :
                               String.valueOf(answer.getSentiment()),
                        questionType,
                        questionNumber));
            }
          }
        }
        feedback.add(new Feedback(templateTitle, feedbackAnswers));
      }

      return feedback;
    }

    private LocalDateRange getDateRange() {
      // Return date range based on reviewPeriodId (defaulting to this year).
      ReviewPeriod reviewPeriod = reviewPeriodServices.findById(reviewPeriodId);
      if (reviewPeriod == null) {
        LocalDate closeDate = LocalDate.now();
        LocalDate startDate = closeDate.withMonth(Month.JANUARY.getValue())
                                .withDayOfMonth(1);
        LocalDate endDate = closeDate.withMonth(Month.DECEMBER.getValue())
                                .withDayOfMonth(31);
        return new LocalDateRange(startDate, endDate);
      } else {
        LocalDate startDate = reviewPeriod.getPeriodStartDate().toLocalDate();
        LocalDate endDate = reviewPeriod.getPeriodEndDate().toLocalDate();
        return new LocalDateRange(startDate, endDate);
      }
    }
}
