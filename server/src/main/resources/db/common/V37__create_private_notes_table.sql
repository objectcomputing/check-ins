drop table if exists private_notes;
CREATE TABLE private_notes(
   id varchar PRIMARY KEY,
   checkinId varchar REFERENCES checkins(id) UNIQUE,
   createdById varchar REFERENCES member_profile(id),
   description varchar
);
