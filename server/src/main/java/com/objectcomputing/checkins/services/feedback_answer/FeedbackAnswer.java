package com.objectcomputing.checkins.services.feedback_answer;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.annotation.sql.ColumnTransformer;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@Introspected
@Table(name = "feedback_answers")
public class FeedbackAnswer {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback answer")
    private UUID id;

    @Column(name = "answer")
    @ColumnTransformer(
            read = "pgp_sym_decrypt(answer::bytea,'${aes.key}')",
            write = "pgp_sym_encrypt(?,'${aes.key}')"
    )
    @Nullable
    @TypeDef(type = DataType.STRING)
    @Schema(description = "the content of the answer")
    private String answer;

    @Column(name = "question_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the feedback question the answer is linked to")
    private UUID questionId;

    @Column(name = "request_id")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the request this question is linked to ")
    private UUID requestId;

    @Column(name = "sentiment")
    @Nullable
    @TypeDef(type = DataType.DOUBLE)
    @Schema(description = "the sentiment of the answer")
    private Double sentiment;

    public FeedbackAnswer(@Nullable String answer, UUID questionId, UUID requestId, @Nullable Double sentiment) {
        this.id = null;
        this.questionId = questionId;
        this.answer = answer;
        this.requestId = requestId;
        this.sentiment = sentiment;
    }

    public FeedbackAnswer(UUID id, @Nullable String answer, @Nullable Double sentiment) {
        this.id = id;
        this.answer = answer;
        this.sentiment = sentiment;
    }

    public FeedbackAnswer() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedbackAnswer that = (FeedbackAnswer) o;
        return Objects.equals(id, that.id) && Objects.equals(answer, that.answer) && Objects.equals(questionId, that.questionId) && Objects.equals(requestId, that.requestId) && Objects.equals(sentiment, that.sentiment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, answer, questionId, requestId, sentiment);
    }

    @Override
    public String toString() {
        return "FeedbackAnswer{" +
                "id=" + id +
                ", answer='" + answer +
                ", questionId=" + questionId +
                ", requestId=" + requestId +
                ", sentiment=" + sentiment +
                '}';
    }
}
