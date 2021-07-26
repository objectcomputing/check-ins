package com.objectcomputing.checkins.services.frozen_template_questions;

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
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "frozen_template_questions")
public class FrozenTemplateQuestion {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the request question answer entry", required = true)
    private UUID id;

    @Column(name = "frozen_template_id")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the versioned template (and by extension, feedback request) that question is attached to ", required = true)
    private UUID frozenTemplateId;

    @Column(name = "question")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(question::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    @Schema(description = "The question asked to the recipient", required = true)
    private String question;

    @Column(name = "question_number")
    @NotBlank
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "Order number of the question relative to others in its set", required = true)
    private Integer questionNumber;

    public FrozenTemplateQuestion(UUID frozenTemplateId, String question, Integer questionNumber) {
        this.id = null;
        this.frozenTemplateId=frozenTemplateId;
        this.question = question;
        this.questionNumber = questionNumber;
    }

    public FrozenTemplateQuestion() {}


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFrozenTemplateId() {
        return frozenTemplateId;
    }

    public void setFrozenTemplateId(UUID frozenTemplateId) {
        this.frozenTemplateId = frozenTemplateId;
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

    public void setQuestionNumber(Integer questionNumber) {
        this.questionNumber = questionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FrozenTemplateQuestion that = (FrozenTemplateQuestion) o;
        return id.equals(that.id) && frozenTemplateId.equals(that.frozenTemplateId) && question.equals(that.question) && questionNumber.equals(that.questionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, frozenTemplateId, question, questionNumber);
    }

    @Override
    public String toString() {
        return "FrozenTemplateQuestion{" +
                "id=" + id +
                ", frozenTemplateId=" + frozenTemplateId +
                ", question='" + question + '\'' +
                ", orderNum=" + questionNumber +
                '}';
    }











}