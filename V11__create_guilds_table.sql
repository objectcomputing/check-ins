drop table if exists guilds;
CREATE TABLE guilds(
   guildid varchar PRIMARY KEY,
   name varchar UNIQUE,
   description varchar
);

drop table if exists guildMembers;
CREATE TABLE guildMembers(
   id varchar PRIMARY KEY,
   guildid varchar REFERENCES guilds(guildid),
   memberid varchar REFERENCES member_profile(uuid),
   lead boolean default false,
   UNIQUE(guildid, memberid)
);
