drop table if exists tags;

CREATE TABLE tags(
   id varchar PRIMARY KEY,
   name varchar UNIQUE

);

CREATE TABLE entity_tags(
    id varchar PRIMARY KEY,
    entity_id varchar,
    tag_id varchar REFERENCES tags(id),
    type varchar

);