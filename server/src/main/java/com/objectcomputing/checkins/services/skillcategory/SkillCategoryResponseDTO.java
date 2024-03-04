package com.objectcomputing.checkins.services.skillcategory;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class SkillCategoryResponseDTO {
    private String id;

    private String name;
    private String description;

    private List<String> skills;

}
