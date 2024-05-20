package com.objectcomputing.checkins.services.feedback_template.template_question;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "template_questions")
public class TemplateQuestion {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback question")
    private UUID id;

    @Column(name = "question")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(question::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "text of the question to receive feedback on")
    private String question;

    @Column(name = "template_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template this question is a part of")
    private UUID templateId;

    @Column(name = "question_number")
    @NotNull
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "order of question in template")
    private Integer questionNumber;

    @Column(name = "input_type")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the type of input used to answer the question")
    public String inputType;

    /**
     * Constructs a new {@link TemplateQuestion} to save
     *
     * @param question The content of the question
     * @param templateId The ID of the feedback template this question is part of
     * @param questionNumber The order of the question in the template
     * @param inputType The type of input used to answer the question
     */
    public TemplateQuestion(@NotBlank String question, @NotNull UUID templateId, @NotNull Integer questionNumber, @NotBlank String inputType) {
        this.id = null;
        this.question = question;
        this.templateId = templateId;
        this.questionNumber = questionNumber;
        this.inputType = inputType;
    }

    /**
     * Constructs a {@link TemplateQuestion} to update
     *
     * @param id The {@link UUID} of the existing {@link TemplateQuestion}
     * @param question The content of the question
     * @param questionNumber The order of the question in the template
     * @param inputType The type of input used to answer the question
     */
    public TemplateQuestion(@NotNull UUID id, @NotBlank String question, @NotNull Integer questionNumber, @NotBlank String inputType) {
        this.id = id;
        this.question = question;
        this.questionNumber = questionNumber;
        this.inputType = inputType;
    }

    /**
     * Constructs a {@link TemplateQuestion} initially detached from a template
     *
     * @param question The content of the question
     * @param questionNumber The order of the question in the template
     * @param inputType The type of input used to answer the question
     */
    public TemplateQuestion(@NotBlank String question, @NotNull Integer questionNumber, @NotBlank String inputType) {
        this.id = null;
        this.templateId = null;
        this.question = question;
        this.questionNumber = questionNumber;
        this.inputType = inputType;
    }

    public TemplateQuestion() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateQuestion that = (TemplateQuestion) o;
        return Objects.equals(id, that.id) && Objects.equals(question, that.question) && Objects.equals(templateId, that.templateId) && Objects.equals(questionNumber, that.questionNumber) && Objects.equals(inputType, that.inputType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, templateId, questionNumber, inputType);
    }

    @Override
    public String toString() {
        return "TemplateQuestion{" +
                "id=" + id +
                ", question='" + question +
                ", templateId=" + templateId +
                ", questionNumber=" + questionNumber +
                ", inputType='" + inputType +
                '}';
    }
}
