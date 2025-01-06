package com.objectcomputing.checkins.services.reports;

import com.objectcomputing.checkins.services.memberprofile.MemberProfileServices;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileUtils;
import com.objectcomputing.checkins.services.permissions.Permission;
import com.objectcomputing.checkins.services.permissions.RequiredPermission;
import com.objectcomputing.checkins.services.kudos.KudosRepository;
import com.objectcomputing.checkins.services.kudos.kudos_recipient.KudosRecipientRepository;
import com.objectcomputing.checkins.services.memberprofile.MemberProfileRepository;
import com.objectcomputing.checkins.services.reviews.ReviewPeriodServices;
import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplateServices;
import com.objectcomputing.checkins.services.feedback_request.FeedbackRequestServices;
import com.objectcomputing.checkins.services.feedback_answer.FeedbackAnswerServices;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionServices;
import com.objectcomputing.checkins.services.employee_hours.EmployeeHoursServices;
import com.objectcomputing.checkins.services.file.FileServices;

import net.steppschuh.markdowngenerator.*;
import net.steppschuh.markdowngenerator.text.heading.Heading;
import net.steppschuh.markdowngenerator.text.emphasis.ItalicText;
import net.steppschuh.markdowngenerator.list.UnorderedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.function.Predicate;
import java.io.IOException;

class MarkdownGeneration {
    class AnswerComparator implements java.util.Comparator<Feedback.Answer> {
        @Override
        public int compare(Feedback.Answer a, Feedback.Answer b) {
            return a.getNumber() - b.getNumber();
        }
    }

    class PositionComparator implements java.util.Comparator<PositionHistory.Position> {
        @Override
        public int compare(PositionHistory.Position a,
                           PositionHistory.Position b) {
            LocalDate left = a.date();
            LocalDate right = b.date();
            if (left.isBefore(right)) {
                return -1;
            } else if (left.isEqual(right)) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    class CompensationComparator implements java.util.Comparator<CompensationHistory.Compensation> {
        @Override
        public int compare(CompensationHistory.Compensation a,
                           CompensationHistory.Compensation b) {
            LocalDate left = a.startDate();
            LocalDate right = b.startDate();
            if (left.isBefore(right)) {
                return -1;
            } else if (left.isEqual(right)) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(MarkdownGeneration.class);
    private static final String noneAvailable = "None available during the period covered by this review.";
    public static final String directory = "merit-reports";

    private final ReportDataServices reportDataServices;
    private final KudosRepository kudosRepository;
    private final KudosRecipientRepository kudosRecipientRepository;
    private final MemberProfileServices memberProfileServices;
    private final ReviewPeriodServices reviewPeriodServices;
    private final FeedbackTemplateServices feedbackTemplateServices;
    private final FeedbackRequestServices feedbackRequestServices;
    private final FeedbackAnswerServices feedbackAnswerServices;
    private final TemplateQuestionServices templateQuestionServices;
    private final EmployeeHoursServices employeeHoursServices;
    private final FileServices fileServices;

    public MarkdownGeneration(ReportDataServices reportDataServices,
                              KudosRepository kudosRepository,
                              KudosRecipientRepository kudosRecipientRepository,
                              MemberProfileServices memberProfileServices,
                              ReviewPeriodServices reviewPeriodServices,
                              FeedbackTemplateServices feedbackTemplateServices,
                              FeedbackRequestServices feedbackRequestServices,
                              FeedbackAnswerServices feedbackAnswerServices,
                              TemplateQuestionServices templateQuestionServices,
                              EmployeeHoursServices employeeHoursServices,
                              FileServices fileServices) {
        this.reportDataServices = reportDataServices;
        this.kudosRepository = kudosRepository;
        this.kudosRecipientRepository = kudosRecipientRepository;
        this.memberProfileServices = memberProfileServices;
        this.reviewPeriodServices = reviewPeriodServices;
        this.feedbackTemplateServices = feedbackTemplateServices;
        this.feedbackRequestServices = feedbackRequestServices;
        this.feedbackAnswerServices = feedbackAnswerServices;
        this.templateQuestionServices = templateQuestionServices;
        this.employeeHoursServices = employeeHoursServices;
        this.fileServices = fileServices;
    }

    void upload(List<UUID> memberIds, UUID reviewPeriodId) {
        for (UUID memberId : memberIds) {
            ReportDataCollation data = new ReportDataCollation(
                                           memberId, reviewPeriodId,
                                           kudosRepository,
                                           kudosRecipientRepository,
                                           memberProfileServices,
                                           reviewPeriodServices,
                                           reportDataServices,
                                           feedbackTemplateServices,
                                           feedbackRequestServices,
                                           feedbackAnswerServices,
                                           templateQuestionServices,
                                           employeeHoursServices);
            generateAndStore(data);
        }
    }

    void generateAndStore(ReportDataCollation data) {
        final String markdown = generate(data);
        store(data, markdown);
    }

    String generate(ReportDataCollation data) {
        StringBuilder sb = new StringBuilder();
        title(data, sb);
        currentInfo(data, sb);
        kudos(data, sb);
        reviewsImpl("Self-Review", data.getSelfReviews(), false, sb);
        reviewsImpl("Reviews", data.getReviews(), true, sb);
        feedback(data, sb);
        titleHistory(data, sb);
        employeeHours(data, sb);
        compensation(data, sb);
        compensationHistory(data, sb);
        sb.append(new Heading("Reviewer Notes", 4)).append("\n");
        return sb.toString();
    }

    void store(ReportDataCollation data, String markdown) {
        // Send this text over to be uploaded to the google drive.
        fileServices.uploadDocument(directory,
                                    data.getMemberProfile().getWorkEmail(),
                                    markdown);
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    private void title(ReportDataCollation data, StringBuilder sb) {
        MemberProfile profile = data.getMemberProfile();
        sb.append(new Heading(MemberProfileUtils.getFullName(profile), 1))
          .append("\n")
          .append(profile.getTitle()).append("\n\n")
          .append("Review Period: ")
          .append(formatDate(data.getStartDate()))
          .append(" - ")
          .append(formatDate(data.getEndDate())).append("\n");
    }

    private void currentInfo(ReportDataCollation data, StringBuilder sb) {
        MemberProfile profile = data.getMemberProfile();
        CurrentInformation.Information current = data.getCurrentInformation();
        String bio = current.biography();
        ZonedDateTime zdt = ZonedDateTime.of(
                                profile.getStartDate().atTime(0, 0),
                                ZoneId.systemDefault());
        long ms = System.currentTimeMillis() - zdt.toInstant().toEpochMilli();
        double years = TimeUnit.DAYS.convert(ms, TimeUnit.MILLISECONDS) / 365.25;
        sb.append(new Heading("Current Information", 1)).append("\n")
          .append(String.format("%.1f", years)).append(" years\n\n")
          .append(new Heading("Biographical Notes", 2)).append("\n")
          .append(bio.isEmpty() ? noneAvailable : bio).append("\n\n");
    }

    private void kudos(ReportDataCollation data, StringBuilder sb) {
        List<ReportKudos> received = data.getKudos();
        sb.append(new Heading("Kudos", 1)).append("\n\n");
        if (received.isEmpty()) {
            sb.append(noneAvailable).append("\n\n");
        } else {
            for (ReportKudos kudo : received) {
                sb.append(kudo.message()).append("\n\n")
                  .append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                  .append(new ItalicText("Submitted on " +
                                         formatDate(kudo.dateCreated()) +
                                         ", by " + kudo.sender()))
                  .append("\n\n\n");
            }
        }
    }

    private Map<String, LocalDate> getUniqueMembers(List<Feedback.Answer> answers) {
        Map<String, LocalDate> members = new HashMap<>();
        List<Feedback.Answer> sorted = new ArrayList<>(answers);
        Collections.sort(sorted, new AnswerComparator());
        for (Feedback.Answer answer : sorted) {
            if (!members.containsKey(answer.getMemberName())) {
                members.put(answer.getMemberName(), answer.getSubmitted());
            }
        }
        return members;
    }

    private Map<String, List<List<String>>> getUniqueQuestions(List<Feedback.Answer> answers) {
        Map<String, List<List<String>>> questions = new HashMap<>();
        for (Feedback.Answer answer : answers) {
            String key = answer.getQuestion();
            if (!questions.containsKey(key)) {
                questions.put(key, new ArrayList<List<String>>());
            }
            List list = new ArrayList<String>();
            list.add(answer.getMemberName());
            list.add(answer.getAnswer());
            questions.get(key).add(list);
        }
        return questions;
    }

    private void reviewsImpl(String title, List<Feedback> feedbackList, boolean listMembers, StringBuilder sb) {
        sb.append(new Heading(title, 1)).append("\n");
        if (feedbackList.isEmpty()) {
            sb.append(noneAvailable).append("\n\n");
        } else {
            for (Feedback feedback : feedbackList) {
                Map<String, LocalDate> members =
                                       getUniqueMembers(feedback.getAnswers());
                for(Map.Entry<String, LocalDate> entry : members.entrySet()) {
                    if (listMembers) {
                        sb.append(entry.getKey()).append(": ");
                    }
                    sb.append("Submitted - ")
                      .append(formatDate(entry.getValue())).append("\n\n");
                }
                sb.append("\n");

                Map<String, List<List<String>>> questions =
                                      getUniqueQuestions(feedback.getAnswers());
                for (Map.Entry<String, List<List<String>>> question : questions.entrySet()) {
                    sb.append(new Heading(question.getKey(), 4)).append("\n");
                    for (List<String> answer : question.getValue()) {
                        if (listMembers) {
                            sb.append(answer.get(0)).append(": ");
                        }
                        sb.append(answer.get(1)).append("\n\n");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }
    }

    private void feedback(ReportDataCollation data, StringBuilder sb) {
        sb.append(new Heading("Feedback", 1)).append("\n");
        List<Feedback> feedbackList = data.getFeedback();
        if (feedbackList.isEmpty()) {
            sb.append(noneAvailable).append("\n\n");
        } else {
            for (Feedback feedback : feedbackList) {
                sb.append(new Heading("Template: " + feedback.getName(), 2))
                  .append("\n");

                Map<String, LocalDate> members =
                                       getUniqueMembers(feedback.getAnswers());
                for (Map.Entry<String, LocalDate> entry : members.entrySet()) {
                    sb.append(entry.getKey()).append(": ");
                    sb.append(formatDate(entry.getValue())).append("\n\n");
                }
                sb.append("\n");

                Map<String, List<List<String>>> questions =
                                      getUniqueQuestions(feedback.getAnswers());
                for (Map.Entry<String, List<List<String>>> question : questions.entrySet()) {
                    sb.append(new Heading(question.getKey(), 4)).append("\n");
                    for (List<String> answer : question.getValue()) {
                        sb.append(answer.get(0)).append(": ");
                        sb.append(answer.get(1)).append("\n\n");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }
    }

    private void titleHistory(ReportDataCollation data, StringBuilder sb) {
        List<PositionHistory.Position> posHistory =
            new ArrayList<>(data.getPositionHistory());
        Collections.sort(posHistory, new PositionComparator());
        sb.append(new Heading("Title History", 2)).append("\n");
        List<String> positions = new ArrayList<>();
        for (PositionHistory.Position position : posHistory) {
            positions.add(String.valueOf(position.date().getYear()) + " - " +
                          position.title());
        }
        sb.append(new UnorderedList<>(positions)).append("\n\n");
    }

    private void compensation(ReportDataCollation data, StringBuilder sb) {
        CurrentInformation.Information current = data.getCurrentInformation();
        sb.append(new Heading("Compensation and Commitments", 2)).append("\n")
          .append("$").append(String.format("%.2f", current.salary()))
          .append(" Base Salary\n\n")
          .append("OCI Range for role: ").append(current.range())
          .append("\n\n");
        String commitments = current.commitments();
        if (commitments == null || commitments.isEmpty()) {
            sb.append("No current bonus commitments\n");
        } else {
            sb.append("Commitments: ").append(current.commitments())
              .append("\n");
        }
        sb.append("\n");
    }

    private List<CompensationHistory.Compensation> prepareCompHistory(ReportDataCollation data, Predicate<? super CompensationHistory.Compensation> fn) {
        List<CompensationHistory.Compensation> comp =
            data.getCompensationHistory().stream()
                .filter(fn).collect(Collectors.toList());
        Collections.sort(comp, new CompensationComparator());
        return comp.subList(0, Math.min(3, comp.size()));
    }

    private void compensationHistory(ReportDataCollation data, StringBuilder sb) {
        List<CompensationHistory.Compensation> compBase =
            prepareCompHistory(data, comp -> comp.amount() != null &&
                                             !comp.amount().isEmpty());
        List<CompensationHistory.Compensation> compTotal =
            prepareCompHistory(data, comp -> comp.totalComp() != null &&
                                             !comp.totalComp().isEmpty());
        sb.append(new Heading("Compensation History", 2)).append("\n")
          .append(new Heading("Base Compensation (annual or hourly)", 3))
          .append("\n");
        List<String> list = new ArrayList<>();
        final String compFormat = "%.2f";
        for (CompensationHistory.Compensation comp : compBase) {
            String value = comp.amount();
            try {
                double val = Double.parseDouble(value);
                value = String.format(compFormat, val);
            } catch (Exception e) {
            }
            list.add(formatDate(comp.startDate()) + " - $" + value);
        }
        sb.append(new UnorderedList<>(list)).append("\n\n")
          .append(new Heading("Total Compensation", 3))
          .append("\n");
        list.clear();
        for (CompensationHistory.Compensation comp : compTotal) {
            LocalDate startDate = comp.startDate();
            String date = startDate.getMonthValue() == 0 &&
                          startDate.getDayOfMonth() == 1 ?
                              String.valueOf(startDate.getYear()) :
                              formatDate(startDate);
            list.add(date + " - " + comp.totalComp());
        }
        sb.append(new UnorderedList<>(list)).append("\n\n");
    }

    private void employeeHours(ReportDataCollation data, StringBuilder sb) {
        sb.append(new Heading("Employee Hours", 2)).append("\n");
        List<String> list = new ArrayList<>();
        ReportHours hours = data.getReportHours();
        final String hourFormat = "%.2f";
        list.add("Contribution Hours: " +
                 String.format(hourFormat, hours.contributionHours()));
        list.add("PTO Hours: " + String.format(hourFormat, hours.ptoHours()));
        list.add("Overtime Hours: " +
                 String.format(hourFormat, hours.overtimeHours()));
        list.add("Billable Utilization: " +
                 String.format(hourFormat, hours.billableUtilization()));
        sb.append(new UnorderedList<>(list)).append("\n\n");
    }
}

