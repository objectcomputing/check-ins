drop table if exists guilds;
CREATE TABLE guilds(
   guildId varchar PRIMARY KEY,
   name varchar UNIQUE,
   description varchar
);

drop table if exists guildMembers;
CREATE TABLE guildMembers(
   guildId varchar REFERENCES guilds(guildId),
   memberId varchar REFERENCES member_profile(uuid),
   isLead boolean default false,
   PRIMARY KEY(guildId, memberId)
);
