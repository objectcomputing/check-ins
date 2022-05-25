DROP TABLE IF EXISTS emails;

CREATE TABLE emails(
    id varchar PRIMARY KEY,
    subject varchar,
    contents varchar,
    sentby varchar REFERENCES member_profile(id),
    recipient varchar REFERENCES member_profile(id),
    senddate timestamp,
    transmissiondate timestamp
);