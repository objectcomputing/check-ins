drop table if exists memberProfile;
CREATE TABLE memberProfile (
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
   teamMemberId varchar REFERENCES memberProfile(uuid),
   pdlId varchar,
   checkInDate date,
   targetQtr varchar,
   targetYear varchar
);
