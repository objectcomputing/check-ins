package com.objectcomputing.checkins.services.feedback_request_questions;

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
@Table(name = "feedback_request_questions")
public class FeedbackRequestQuestion {
    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the request question answer entry", required = true)
    private UUID id;

    @Column(name = "question")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(question::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    @Schema(description = "The question asked to the recipient", required = true)
    private String question;

    @Column(name = "request_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback request the question answer pair is attached to", required = true)
    private UUID requestId;

    @Column(name = "question_number")
    @NotNull
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "Order number of the question relative to others in its set", required = true)
    private Integer questionNumber;

    public FeedbackRequestQuestion(String question, UUID requestId, Integer questionNumber) {
        this.id = null;
        this.requestId=requestId;
        this.question = question;
        this.questionNumber = questionNumber;
    }

    public FeedbackRequestQuestion() {}

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

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
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
        FeedbackRequestQuestion question1 = (FeedbackRequestQuestion) o;
        return Objects.equals(id, question1.id) && Objects.equals(requestId, question1.requestId) && Objects.equals(question, question1.question) && Objects.equals(questionNumber, question1.questionNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, question, questionNumber);
    }

    @Override
    public String toString() {
        return "FeedbackRequestQuestion{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", question='" + question +
                ", questionNumber=" + questionNumber +
                '}';
    }
}
