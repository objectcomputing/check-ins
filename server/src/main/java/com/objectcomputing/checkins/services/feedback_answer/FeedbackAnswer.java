package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
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
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback answer", required = true)
    private UUID id;

    @Column(name = "questionId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback question the answer is linked to", required = true)
    private String questionId;

    @Column(name="answer")
    @NotBlank
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the content of the answer", required = true)
    private String answer;

    @Column(name = "sentiment")
    @Nullable
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the sentiment of the answer")
    private String sentiment;

    public FeedbackAnswer(String questionId, String answer, @Nullable String sentiment) {
        this.id = null;
        this.questionId = questionId;
        this.answer = answer;
        this.sentiment = sentiment;
    }

    public FeedbackAnswer() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Nullable
    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(@Nullable String sentiment) {
        this.sentiment = sentiment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackAnswer that = (FeedbackAnswer) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(questionId, that.questionId) &&
                Objects.equals(answer, that.answer) &&
                Objects.equals(sentiment, that.sentiment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, questionId, answer, sentiment);
    }

    @Override
    public String toString() {
        return "FeedbackAnswer{" +
                "id=" + id +
                ", questionId='" + questionId + '\'' +
                ", answer='" + answer + '\'' +
                ", sentiment='" + sentiment + '\'' +
                '}';
    }
}
