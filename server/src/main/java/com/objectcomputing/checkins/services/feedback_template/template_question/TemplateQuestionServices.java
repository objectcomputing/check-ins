package com.objectcomputing.checkins.services.feedback_template.template_question;

import java.util.List;
import java.util.UUID;

public interface TemplateQuestionServices {

    TemplateQuestion update(TemplateQuestion templateQuestion);

    Boolean delete(UUID id);

    TemplateQuestion save(TemplateQuestion feedbackQuestion);

    TemplateQuestion getById(UUID id);

    List<TemplateQuestion> findByFields(UUID templateId);
}
