package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "template_questions")
public class TemplateQuestion {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback question", required = true)
    private UUID id;

    @Column(name = "question")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @Column(name = "templateId")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template this question is a part of", required = true)
    private UUID templateId;

    @Column(name = "questionNumber")
    @NotBlank
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "order of question in template", required = true)
    private Integer questionNumber;

    /**
     * Constructs a new {@link TemplateQuestion} to save
     *
     * @param question The content of the question
     * @param templateId The ID of the feedback template this question is part of
     * @param questionNumber The order of the question in the template
     */
    public TemplateQuestion(@NotBlank String question, @NotBlank UUID templateId, @NotBlank Integer questionNumber) {
        this.id = null;
        this.question = question;
        this.templateId = templateId;
        this.questionNumber = questionNumber;
    }

    /**
     * Constructs a {@link TemplateQuestion} to update
     *
     * @param id The {@link UUID} of the existing {@link TemplateQuestion}
     * @param question The content of the question
     * @param questionNumber The order of the question in the template
     */
    public TemplateQuestion(@NotBlank UUID id, @NotBlank String question, @NotBlank Integer questionNumber) {
        this.id = id;
        this.question = question;
        this.questionNumber = questionNumber;
    }

    /**
     * Constructs a {@link TemplateQuestion} initially detached from a template
     *
     * @param question The content of the question
     * @param questionNumber The order of the question in the template
     */
    public TemplateQuestion(@NotBlank String question, @NotBlank Integer questionNumber) {
        this.id = null;
        this.templateId = null;
        this.question = question;
        this.questionNumber = questionNumber;
    }

    public TemplateQuestion() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(Integer orderNum) {
        this.questionNumber = orderNum;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateQuestion that = (TemplateQuestion) o;
        return Objects.equals(id, that.id) && Objects.equals(question, that.question) && Objects.equals(questionNumber, that.questionNumber) && Objects.equals(templateId, that.templateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, questionNumber, templateId);
    }

    @Override
    public String toString() {
        return "TemplateQuestion{" +
                "id=" + id +
                ", question='" + question +
                ", questionNumber=" + questionNumber +
                ", templateId=" + templateId +
                '}';
    }
}
