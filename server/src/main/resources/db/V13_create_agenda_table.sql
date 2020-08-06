drop table if exists agenda_items;
CREATE TABLE agenda_items(
   uuid varchar PRIMARY KEY,
   checkinId varchar REFERENCES checkin(id),
   createdById varchar REFERENCES member_profile(uuid),
   description varchar
);