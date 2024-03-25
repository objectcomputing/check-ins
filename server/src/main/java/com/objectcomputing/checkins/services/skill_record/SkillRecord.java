package com.objectcomputing.checkins.services.skill_record;

import com.objectcomputing.checkins.services.skillcategory.skillcategory_skill.SkillCategorySkillId;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;

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

    @Column(name = "extraneous")
    private boolean extraneous;

    @Column(name = "pending")
    private boolean pending;

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

    public boolean isExtraneous() {
        return extraneous;
    }

    public void setExtraneous(boolean extraneous) {
        this.extraneous = extraneous;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
