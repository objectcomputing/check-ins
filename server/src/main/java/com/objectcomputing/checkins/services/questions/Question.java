package com.objectcomputing.checkins.services.questions;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;


@Entity
@Table(name ="questions")
public class Question {

    public Question(@NotBlank String text) {
        this.questionid = questionid;
        this.text = text;
    }

    public Question() {
    }

    @Id
    @Column(name="questionid")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    private UUID questionid;

    @NotBlank
    @Column(name="text")
    private String text;

    public UUID getQuestionid() {
        return questionid;
    }

    public void setQuestionid(UUID questionid) {
        this.questionid = questionid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(questionid, question.questionid) &&
                Objects.equals(text, question.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionid, text);
    }

    @Override
    public String toString() {
        return "Question {" +
                "questionid='" + questionid + '\'' +
                "text='" + text + '\'' +
                '}';
    }

}
