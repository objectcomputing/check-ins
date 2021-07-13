package com.objectcomputing.checkins.services.fixture;

import com.objectcomputing.checkins.services.feedback_template.FeedbackTemplate;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestion;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionCreateDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionResponseDTO;
import com.objectcomputing.checkins.services.feedback_template.template_question.TemplateQuestionUpdateDTO;
import com.objectcomputing.checkins.services.guild.Guild;
import com.objectcomputing.checkins.services.guild.GuildCreateDTO;
import com.objectcomputing.checkins.services.guild.GuildUpdateDTO;
import com.objectcomputing.checkins.services.guild.member.GuildMember;
import com.objectcomputing.checkins.services.guild.member.GuildMemberResponseDTO;
import com.objectcomputing.checkins.services.memberprofile.MemberProfile;

import java.util.UUID;

public interface TemplateQuestionFixture extends RepositoryFixture {

    default TemplateQuestion createDefaultFeedbackQuestion() {
        return new TemplateQuestion("How are you doing today?");
    }

    default TemplateQuestion createSecondDefaultFeedbackQuestion() {
        return new TemplateQuestion("How is the project going?");
    }

    default TemplateQuestion saveDefaultFeedbackQuestion(FeedbackTemplate template) {
        return getTemplateQuestionRepository().save(new TemplateQuestion("How are you?", template.getId(), 1));
    }

    default TemplateQuestion saveAnotherDefaultFeedbackQuestion(FeedbackTemplate template) {
        return getTemplateQuestionRepository().save(new TemplateQuestion("What do you know about marsupials?", template.getId(), 2));
    }

    default TemplateQuestionResponseDTO createDefaultTemplateQuestionDTO(FeedbackTemplate template, TemplateQuestion question) {
        return dtoFromEntity(template, createDefaultFeedbackQuestion());
    }

    default TemplateQuestionUpdateDTO updateTemplateQuestionDto(TemplateQuestion questionEntity ){
      TemplateQuestionUpdateDTO dto = new TemplateQuestionUpdateDTO();
        dto.setId(questionEntity.getId());
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


    default TemplateQuestionResponseDTO dtoFromEntity(FeedbackTemplate template, TemplateQuestion questionEntity) {
        TemplateQuestionResponseDTO dto = new TemplateQuestionResponseDTO();
        dto.setId(questionEntity.getId());
        dto.setQuestion(questionEntity.getQuestion());
        dto.setTemplateId(template.getId());
        dto.setOrderNum(questionEntity.getOrderNum());
        return dto;
    }

}
