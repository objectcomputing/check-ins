
drop table if exists checkin_notes;
CREATE TABLE checkin_notes(
   uuid varchar PRIMARY KEY,
   checkinId varchar REFERENCES checkins(id),
   createdById varchar REFERENCES member_profile(uuid),
   privateNotes boolean,
   description varchar
);
