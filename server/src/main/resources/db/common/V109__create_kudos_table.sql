DROP TABLE IF EXISTS kudos;
CREATE TABLE kudos(
    id varchar PRIMARY KEY,
    message varchar,
    senderid varchar REFERENCES member_profile(id),
    teamid varchar REFERENCES team(id),
    datecreated date,
    dateapproved date
);
