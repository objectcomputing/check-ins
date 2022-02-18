package com.objectcomputing.checkins.services.feedback_template.template_question.template_question_values;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
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
@Table(name = "template_question_values")
public class TemplateQuestionValue {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the template question option/value", required = true)
    private UUID id;

    @Column(name = "option_text")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(option_text::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String optionText;

    @Column(name = "question_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template question this option belongs to", required = true)
    private UUID questionId;

    @Column(name = "option_number")
    @NotBlank
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "order of option when displayed as part of the question", required = true)
    private Integer optionNumber;

    public TemplateQuestionValue(String optionText, Integer optionNumber) {
        this.id = null;
        this.optionText = optionText;
        this.questionId = null;
        this.optionNumber = optionNumber;
    }


    public TemplateQuestionValue(UUID id, String optionText, UUID questionId, Integer optionNumber) {
        this.id = id;
        this.optionText = optionText;
        this.questionId = questionId;
        this.optionNumber = optionNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public Integer getOptionNumber() {
        return optionNumber;
    }

    public void setOptionNumber(Integer optionNumber) {
        this.optionNumber = optionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateQuestionValue that = (TemplateQuestionValue) o;
        return id.equals(that.id) && optionText.equals(that.optionText) && questionId.equals(that.questionId) && optionNumber.equals(that.optionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, optionText, questionId, optionNumber);
    }

    @Override
    public String toString() {
        return "TemplateQuestionValue{" +
                "id=" + id +
                ", optionText='" + optionText + '\'' +
                ", questionId=" + questionId +
                ", optionNumber=" + optionNumber +
                '}';
    }
}




