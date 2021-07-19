package com.objectcomputing.checkins.services.frozen_template_questions;

import java.util.List;
import java.util.UUID;

public interface FrozenTemplateQuestionServices {
    FrozenTemplateQuestion save(FrozenTemplateQuestion frozenTemplateQuestion);

    FrozenTemplateQuestion getById(UUID id);

    List<FrozenTemplateQuestion> findByValues(UUID frozenTemplateId);

}
