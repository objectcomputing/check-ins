
drop table if exists checkin_notes;
CREATE TABLE checkin_notes(
   id varchar PRIMARY KEY,
   checkinId varchar REFERENCES checkins(id) UNIQUE,
   createdById varchar REFERENCES member_profile(uuid),
   description varchar
);
