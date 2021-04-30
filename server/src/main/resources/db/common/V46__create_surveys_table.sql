drop table if exists surveys;
CREATE TABLE surveys (
   id varchar PRIMARY KEY,
   createdOn date,
   createdBy varchar REFERENCES member_profile(id),
   name varchar,
   description varchar
);