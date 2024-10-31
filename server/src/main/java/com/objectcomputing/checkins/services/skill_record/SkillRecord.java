package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.skillcategory.skillcategory_skill.SkillCategorySkillId;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@MappedEntity
@Introspected
@Table(name = "skill_record")
@SuppressWarnings("unused")
public class SkillRecord {

    @EmbeddedId
    private SkillCategorySkillId skillCategorySkillId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "extraneous")
    private Boolean extraneous;

    @NotNull
    @Column(name = "pending")
    private Boolean pending;

    @Column(name = "category_name")
    private String categoryName;

    public SkillRecord() {}

    public SkillCategorySkillId getSkillCategorySkillId() {
        return skillCategorySkillId;
    }

    public void setSkillCategorySkillId(SkillCategorySkillId skillCategorySkillId) {
        this.skillCategorySkillId = skillCategorySkillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isExtraneous() {
        return extraneous;
    }

    public void setExtraneous(Boolean extraneous) {
        this.extraneous = extraneous;
    }

    public Boolean isPending() {
        return pending;
    }

    public void setPending(Boolean pending) {
        this.pending = pending;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
