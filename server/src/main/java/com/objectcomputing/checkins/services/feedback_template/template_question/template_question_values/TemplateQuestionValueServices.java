package com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values;

import java.util.List;
import java.util.UUID;

public interface TemplateQuestionValueServices {

    TemplateQuestionValue update(TemplateQuestionValue templateQuestionValue);

    Boolean delete(UUID id);

    TemplateQuestionValue save(TemplateQuestionValue templateQuestionValue);

    TemplateQuestionValue getById(UUID id);

    List<TemplateQuestionValue> findByFields(UUID questionId);
}
