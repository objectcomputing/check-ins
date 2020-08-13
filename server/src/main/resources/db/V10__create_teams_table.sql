drop table if exists team;
CREATE TABLE team (
   teamid varchar PRIMARY KEY,
   name varchar UNIQUE,
   description varchar
);

drop table if exists team_member;
CREATE TABLE team_member (
   id varchar PRIMARY KEY,
   teamid varchar REFERENCES team (teamid),
   memberid varchar REFERENCES member_profile(uuid),
   lead boolean default false,
   UNIQUE(teamid, memberid)
);
