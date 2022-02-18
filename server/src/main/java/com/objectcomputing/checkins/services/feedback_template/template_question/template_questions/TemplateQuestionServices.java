package com.objectcomputing.checkins.services.feedback_template.template_question.template_questions;

import com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values.TemplateQuestionValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.UUID;

public interface TemplateQuestionServices {

    TemplateQuestion update(TemplateQuestion templateQuestion, List<TemplateQuestionValue> questionOptions);

    Boolean delete(UUID id);

    Pair<TemplateQuestion,List<TemplateQuestionValue>> save(TemplateQuestion feedbackQuestion, List<TemplateQuestionValue> questionOptions);

    Pair<TemplateQuestion, List<TemplateQuestionValue>> getById(UUID id);

    List<Pair<TemplateQuestion, List<TemplateQuestionValue>>> findByFields(UUID templateId);
}
