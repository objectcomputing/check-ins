DROP TABLE IF EXISTS kudos_recipient;
CREATE TABLE kudos_recipient(
    id varchar PRIMARY KEY,
    kudosid varchar REFERENCES kudos(id),
    memberid varchar REFERENCES member_profile(id),
    teamid varchar REFERENCES team(id)
);
