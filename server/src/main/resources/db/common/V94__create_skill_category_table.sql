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

DROP VIEW IF EXISTS skill_record;
CREATE VIEW skill_record AS
SELECT s.id AS skill_id,
       COALESCE(sc.id, '6ad7baca-3741-4ae8-b45a-4b82ade40d1f') AS skillcategory_id,
       s.name, s.description, s.extraneous, s.pending, sc.name AS category_name
FROM skills s
         LEFT JOIN skillcategory_skills ss ON s.id = ss.skill_id
         LEFT JOIN skillcategories sc ON ss.skillcategory_id = sc.id;