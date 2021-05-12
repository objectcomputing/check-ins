package com.objectcomputing.checkins.services.questions;

import com.sun.istack.Nullable;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.micronaut.http.annotation.Body;
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

    @Nullable
    @Column(name="categoryId")
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
