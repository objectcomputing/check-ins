package com.objectcomputing.checkins.services.feedback_request_questions;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.jdbc.annotation.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
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

    @Column(name = "requestId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback request the question answer pair is attached to", required = true)
    private UUID requestId;

    @Column(name = "questionContent")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(questionContent::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    @Schema(description = "The question asked to the recipient", required = true)
    private String questionContent;

    @Column(name = "answerContent")
    @Nullable
    @TypeDef(type = DataType.STRING)
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(answerContent::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    @Schema(description = "The answer to the question", required = false)
    private String answerContent;

    @Column(name = "orderNum")
    @NotNull
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "Order number of the question relative to others in its set", required = true)
    private Integer orderNum;

    public FeedbackRequestQuestion(UUID requestId, String questionContent, Integer orderNum) {
        this.id = null;
        this.requestId=requestId;
        this.questionContent = questionContent;
        this.answerContent = null;
        this.orderNum = orderNum;
    }


    public FeedbackRequestQuestion() {}

    public FeedbackRequestQuestion(UUID id, UUID requestId, String questionContent, String answerContent, Integer orderNum) {
        this.id = id;
        this.requestId = requestId;
        this.questionContent = questionContent;
        this.answerContent = answerContent;
        this.orderNum = orderNum;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackRequestQuestion that = (FeedbackRequestQuestion) o;
        return id.equals(that.id) && orderNum.equals(that.orderNum) && requestId.equals(that.requestId) && questionContent.equals(that.questionContent) && Objects.equals(answerContent, that.answerContent);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, questionContent, answerContent, orderNum);
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "FeedbackRequestQuestion{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", questionContent='" + questionContent + '\'' +
                ", answerContent='" + answerContent + '\'' +
                ", orderNum='" + orderNum + '\'' +
                '}';
    }


}
