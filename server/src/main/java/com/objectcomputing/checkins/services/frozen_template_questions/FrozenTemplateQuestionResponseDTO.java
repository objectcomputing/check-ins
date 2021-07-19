package com.objectcomputing.checkins.services.frozen_template_questions;

import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class FrozenTemplateQuestionResponseDTO {

    @NotNull
    @Schema(description = "unique id of the request question answer entry", required = true)
    private UUID id;

    @NotNull
    @Schema(description = "id of the versioned template (and by extension, feedback request) that question is attached to ", required = true)
    private UUID frozenTemplateId;


    @NotBlank
    @Schema(description = "The question asked to the recipient", required = true)
    private String questionContent;

    @NotNull
    @Schema(description = "Order number of the question relative to others in its set", required = true)
    private Integer orderNum;

    public UUID getFrozenTemplateId() {
        return frozenTemplateId;
    }

    public void setFrozenTemplateId(UUID frozenTemplateId) {
        this.frozenTemplateId = frozenTemplateId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
