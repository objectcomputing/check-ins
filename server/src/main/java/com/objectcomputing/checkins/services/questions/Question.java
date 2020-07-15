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
import java.util.UUID;


@Entity
@Table(name ="questions")
public class Question {

    public Question(UUID questionid, @NotBlank @NotNull String text) {
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
    @NotNull
    @Column(name="text", unique = true)
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
    public String toString() {
        return "Question {" +
                "text='" + text + '\'' +
                '}';
    }

}
