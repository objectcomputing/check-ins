drop table if exists memberProfile;
CREATE TABLE memberProfile(
   uuid uuid PRIMARY KEY,
   name varchar,
   role varchar,
   pdlId bigint,
   location varchar,
   workEmail varchar,
   insperityId varchar,
   startDate date,
   bioText varchar
);


