package com.objectcomputing.checkins.services.feedback.feedback_answer;

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
@Table(name = "feedback_answers")
public class FeedbackAnswer {
    @Id
    @Column(name = "entryId")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "ID of the feedback answer entry in the database", required = true)
    private UUID entryId;

    @Column(name = "answer")
    @NotBlank
    @Schema(description = "content of the feedback", required = true)
    @ColumnTransformer(
            read =  "pgp_sym_decrypt(content::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    private String answer;

    @Column(name = "id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of member profile who answered question", required = true)
    private UUID id;

    @Column(name = "questionId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of question the answer is attached to", required = true)
    private UUID questionId;

    @Column(name = "requestId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback request the answer is attached to", required = true)
    private UUID requestId;

    @Column(name = "sentiment")
    @Nullable
    @TypeDef(type = DataType.FLOAT)
    @Schema(description = "The machine-analyzed sentiment of the feedback answer", required = false)
    private Float sentiment;

    public FeedbackAnswer(UUID entryId, String answer, UUID id, UUID questionId, UUID requestId, @Nullable Float sentiment) {
        this.entryId = entryId;
        this.answer = answer;
        this.id = id;
        this.questionId = questionId;
        this.requestId = requestId;
        this.sentiment = sentiment;
    }

    public FeedbackAnswer(String answer, UUID id, UUID questionId, UUID requestId, @Nullable Float sentiment) {
        this.entryId = null;
        this.answer = answer;
        this.id = id;
        this.questionId = questionId;
        this.requestId = requestId;
        this.sentiment = sentiment;
    }


    public UUID getEntryId() {
        return entryId;
    }

    public void setEntryId(UUID entryId) {
        this.entryId = entryId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    @Nullable
    public Float getSentiment() {
        return sentiment;
    }

    public void setSentiment(@Nullable Float sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public String toString() {
        return "FeedbackAnswer{" +
                "entryId=" + entryId +
                ", answer='" + answer + '\'' +
                ", id=" + id +
                ", questionId=" + questionId +
                ", requestId=" + requestId +
                ", sentiment=" + sentiment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackAnswer that = (FeedbackAnswer) o;
        return entryId.equals(that.entryId) && answer.equals(that.answer) && id.equals(that.id) && questionId.equals(that.questionId) && requestId.equals(that.requestId) && Objects.equals(sentiment, that.sentiment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryId, answer, id, questionId, requestId, sentiment);
    }




}
