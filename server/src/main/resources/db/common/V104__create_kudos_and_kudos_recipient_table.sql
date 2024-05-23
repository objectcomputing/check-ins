DROP TABLE IF EXISTS kudos;
CREATE TABLE kudos(
    id varchar PRIMARY KEY,
    message varchar,
    senderid varchar REFERENCES member_profile(id),
    recipientid varchar REFERENCES member_profile(id),
    teamid varchar REFERENCES team(id),
    datecreated date,
    dateapproved date,
    publiclyvisible boolean
);

DROP TABLE IF EXISTS kudos_recipient;
CREATE TABLE kudos_recipient(
    id varchar PRIMARY KEY,
    kudosid varchar REFERENCES kudos(id),
    memberid varchar REFERENCES member_profile(id)
);