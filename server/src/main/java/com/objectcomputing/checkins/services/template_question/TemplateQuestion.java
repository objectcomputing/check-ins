package com.objectcomputing.checkins.services.template_question;

import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Table(name = "template_questions")
public class TemplateQuestion {

    @Id
    @Column(name = "id")
    @AutoPopulated
    @TypeDef(type = DataType.STRING)
    @Schema(description = "unique id of the feedback question", required = true)
    private UUID id;

    @Column(name = "question")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "text of the question to receive feedback on", required = true)
    private String question;

    @Column(name = "templateId")
    @NotNull
    @TypeDef(type = DataType.STRING)
    @Schema(description = "id of the template this question is a part of", required = true)
    private UUID templateId;

    @Column(name = "orderNum")
    @Nullable
    @TypeDef(type = DataType.INTEGER)
    @Schema(description = "order of question in template", required = true)
    private Integer orderNum;

    @Override
    public String toString() {
        return "TemplateQuestion{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", templateId=" + templateId +
                ", orderNum=" + orderNum +
                '}';
    }

    public TemplateQuestion(@NotNull String question,
                            @NotNull UUID templateId, @Nullable Integer orderNum) {
        this.id = null;
        this.question = question;
        this.templateId = templateId;
        this.orderNum = orderNum;
    }



    public TemplateQuestion() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public Integer getOrderNum() { return orderNum; }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }


}
