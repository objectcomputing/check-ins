drop table if exists checkin_document;
CREATE TABLE checkin_document(
   id varchar PRIMARY KEY,
   checkinsId varchar REFERENCES checkins(id),
   uploadDocId varchar UNIQUE
);