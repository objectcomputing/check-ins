package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionUpdateDTO;

public interface TemplateQuestionFixture extends RepositoryFixture {

    default TemplateQuestion createDefaultFeedbackQuestion() {
        return new TemplateQuestion("How are you doing today?");
    }

    default TemplateQuestion createSecondDefaultFeedbackQuestion() {
        return new TemplateQuestion("How is the project going?");
    }

    default TemplateQuestion saveDefaultFeedbackQuestion(FeedbackTemplate template) {
        return getTemplateQuestionRepository().save(new TemplateQuestion("How are you?", template.getId()));
    }


    default TemplateQuestionUpdateDTO updateTemplateQuestionDto(TemplateQuestion questionEntity ){
      TemplateQuestionUpdateDTO dto = new TemplateQuestionUpdateDTO();
        dto.setId(questionEntity.getId());
        dto.setTemplateId(questionEntity.getTemplateId());
        dto.setQuestion(questionEntity.getQuestion());
        dto.setOrderNum(questionEntity.getOrderNum());
        return dto;

    }

    default TemplateQuestionCreateDTO createDefaultTemplateQuestionDto(FeedbackTemplate template, TemplateQuestion question) {
        TemplateQuestionCreateDTO dto = new TemplateQuestionCreateDTO();
        dto.setQuestion(question.getQuestion());
        dto.setTemplateId(template.getId());
        dto.setOrderNum(question.getOrderNum());
        return dto;
    }



}
