DROP TABLE IF EXISTS feedback;

CREATE TABLE feedback (
    id varchar PRIMARY KEY,
    content varchar,
    sentTo varchar REFERENCES member_profile(id),
    sentBy varchar REFERENCES member_profile(id),
    confidential boolean,
    createdOn date,
    updatedOn date
);
