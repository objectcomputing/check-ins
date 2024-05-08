package com.objectcomputing.checkins.services.question_category;

import io.micronaut.core.annotation.Introspected;
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
@Table(name = "question_categories")
public class QuestionCategory {

    @Id
    @Column(name="id")
    @AutoPopulated
    @TypeDef(type= DataType.STRING)
    @Schema(description = "id of the question category this entry is associated with")
    private UUID id;

    @NotBlank
    @Column(name="name")
    @Schema(description = "name of the category for the question")
    private String name;

    public QuestionCategory(UUID id, @NotBlank String name) {
        this.id = id;
        this.name = name;
    }

    public QuestionCategory(@NotBlank String name) {
        this.id = id;
        this.name = name;
    }

    public QuestionCategory() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionCategory)) return false;
        QuestionCategory that = (QuestionCategory) o;
        return getId().equals(that.getId()) && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "QuestionCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
