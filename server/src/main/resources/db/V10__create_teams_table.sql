DROP TABLE IF EXISTS team;
CREATE TABLE team (
  uuid varchar PRIMARY KEY,
  name varchar,
  description varchar
);

DROP TABLE IF EXISTS team_member;
CREATE TABLE team_member (
  uuid varchar PRIMARY KEY,
  teamId varchar REFERENCES team(uuid),
  memberId varchar REFERENCES member_profile(uuid),
  isLead boolean default false
);
