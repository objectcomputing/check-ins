drop table if exists surveys;
CREATE TABLE surveys (
   id varchar PRIMARY KEY,
   name varchar,
   description varchar,
   createdOn date,
   createdBy varchar REFERENCES member_profile(id)
);