drop table if exists action_items;
CREATE TABLE action_items(
   id varchar PRIMARY KEY,
   checkinId varchar REFERENCES checkins(id),
   createdById varchar REFERENCES member_profile(uuid),
   description varchar
);
