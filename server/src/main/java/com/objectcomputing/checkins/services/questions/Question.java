package com.objectcomputing.checkins.services.questions;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
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
@Table(name ="questions")
public class Question {

    public Question(@NotBlank String text) {
        this.text = text;
    }

    public Question() {
    }

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type=DataType.STRING)
    @Schema(description = "id of the question this entry is associated with")
    private UUID id;

    @NotBlank
    @Column(name="text")
    @Schema(description = "text of the question being asked", required = true)
    private String text;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
        return Objects.equals(id, question.id) &&
                Objects.equals(text, question.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }

    @Override
    public String toString() {
        return "Question {" +
                "id='" + id + '\'' +
                "text='" + text + '\'' +
                '}';
    }

}
