drop table if exists opportunities;
CREATE TABLE opportunities (
   id varchar PRIMARY KEY,
   name varchar,
   description varchar,
   url varchar,
   expiresOn date,
   submittedOn date,
   submittedBy varchar REFERENCES member_profile(id)
   pending boolean
);
--drop table if exists opportunities;
--CREATE TABLE surveys (
--   id varchar PRIMARY KEY,
--   name varchar,
--   description varchar,
--   createdOn date,
--   createdBy varchar REFERENCES member_profile(id)
--);