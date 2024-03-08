DROP TABLE if exists skillcategories;
CREATE TABLE skillcategories
(
    id          varchar PRIMARY KEY,
    name        varchar UNIQUE,
    description varchar
);
DROP TABLE if exists skillcategory_skills;
CREATE TABLE skillcategory_skills
(
    skillcategory_id varchar NOT NULL,
    skill_id         varchar NOT NULL,
    unique (skillcategory_id, skill_id),
    constraint fk_skillcategory_skillcategoryskills
        foreign key (skillcategory_id) references skillcategories (id),
    constraint fk_skill_skillcategoryskills
        foreign key (skill_id) references skills(id)
);
