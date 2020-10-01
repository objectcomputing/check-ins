drop table if exists member_profile;
CREATE TABLE member_profile (
   uuid varchar PRIMARY KEY,
   name varchar,
   role varchar,
   pdlId varchar,
   location varchar,
   workEmail varchar,
   insperityId varchar,
   startDate date,
   bioText varchar
);

drop table if exists checkins;
CREATE TABLE checkins (
   id varchar PRIMARY KEY,
   teamMemberId varchar REFERENCES member_profile(uuid),
   pdlId varchar,
   checkInDate date,
   targetQtr varchar,
   targetYear varchar
);
