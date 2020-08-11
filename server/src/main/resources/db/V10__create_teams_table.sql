drop table if exists teams;
CREATE TABLE teams(
   teamid varchar PRIMARY KEY,
   name varchar UNIQUE,
   description varchar
);

drop table if exists teamMembers;
CREATE TABLE teamMembers(
   id varchar PRIMARY KEY,
   teamid varchar REFERENCES teams(teamid),
   memberid varchar REFERENCES member_profile(uuid),
   lead boolean default false,
   UNIQUE(teamid, memberid)
);
