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
import com.objectcomputing.checkins.services.questions.QuestionServices;
import com.objectcomputing.checkins.services.questions.Question;

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
    private QuestionServices questionServices;

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
                          QuestionServices questionServices) {
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
        this.questionServices = questionServices;
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
    public List<Kudos> getKudos() {
        List<KudosRecipient> recipients = kudosRecipientRepository.findByMemberId(memberId);
        List<Kudos> kudosList = new ArrayList<Kudos>();
        for (KudosRecipient recipient : recipients) {
            Kudos kudos = kudosRepository.findById(recipient.getKudosId())
                                         .orElse(null);
            if (kudos != null) {
                LocalDate created = kudos.getDateCreated();
                if ((created.isEqual(startDate) ||
                     created.isAfter(startDate)) && created.isBefore(endDate)) {
                    kudosList.add(kudos);
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

    public List<Feedback> getFeedback() {
      List<Feedback> feedback = new ArrayList<Feedback>();

      // Get the list of requests for the member and review period.
      // We will need to cross-reference the templates.
      List<FeedbackRequest> requests =
        feedbackRequestServices.findByValues(null, memberId, null,
                                             null, reviewPeriodId, null, null);

      // Iterate over each request and find the template.  See if the template
      // can be used for a feedback request.
      Map<UUID, String> templates =
                         new HashMap<UUID, String>();
      for (FeedbackRequest request: requests) {
        if (!templates.containsKey(request.getTemplateId())) {
          try {
            FeedbackTemplate template =
                   feedbackTemplateServices.getById(request.getTemplateId());
            if (template.getActive()) {
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
              try {
                Question question = questionServices.findById(
                                              answer.getQuestionId());
                String questionText = question.getText();
                feedbackAnswers.add(
                    new Feedback.Answer(recipientName, request.getSubmitDate(),
                                        questionText, answer.getAnswer()));
              } catch(NotFoundException ex) {
                LOG.error(ex.toString());
              }
            }
          }
        }
        feedback.add(new Feedback(templateTitle, feedbackAnswers));
      }

      return feedback;
    }

    private LocalDateRange getDateRange() {
        // Return date range based on reviewPeriodId (defaulting to this year).
        LocalDate closeDate = LocalDate.now();
        ReviewPeriod reviewPeriod = reviewPeriodServices.findById(reviewPeriodId);
        if (reviewPeriod != null) {
            LocalDate date = reviewPeriod.getCloseDate().toLocalDate();
            if (date != null) {
                closeDate = date;
            }
        }

        LocalDate startDate = closeDate.withMonth(Month.JANUARY.getValue())
                                .withDayOfMonth(1);
        LocalDate endDate = closeDate.withMonth(Month.DECEMBER.getValue())
                                .withDayOfMonth(31);
        return new LocalDateRange(startDate, endDate);
    }
}
