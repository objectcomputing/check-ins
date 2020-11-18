drop table if exists skill_tags;
CREATE TABLE skill_tags (
   id varchar PRIMARY KEY,
   name varchar,
   description varchar
);

drop table if exists skill_skill_tag;
  CREATE TABLE skill_skill_tag(
     id varchar PRIMARY KEY,
     skill_tag_id varchar REFERENCES skill_tags(id),
     skill_id varchar REFERENCES skills(id)
  );
