package com.objectcomputing.checkins.services.questions;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;
import java.util.UUID;


@Entity
@Introspected
@Table(name = "questions")
public class Question {

    public Question(@NotBlank String text, @Nullable UUID categoryId) {
        this.text = text;
        this.categoryId = categoryId;
    }

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

    @Nullable
    @Column(name="categoryid")
    @TypeDef(type= DataType.STRING)
    @Schema(description = "id of the category this question is associated with")
    private UUID categoryId;

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

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id) &&
                Objects.equals(text, question.text) &&
                Objects.equals(categoryId, question.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, categoryId);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }

}
