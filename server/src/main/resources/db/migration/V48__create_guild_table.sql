DROP TABLE IF EXISTS guild;
CREATE TABLE guild (
  id varchar PRIMARY KEY,
  name varchar,
  description varchar,
  CONSTRAINT guild_uniqueconstraint UNIQUE (name)
);

DROP TABLE IF EXISTS guild_member;
CREATE TABLE guild_member (
  id varchar PRIMARY KEY,
  guildId varchar REFERENCES guild ON DELETE CASCADE,
  memberId varchar REFERENCES member_profile,
  lead boolean default false,
  CONSTRAINT guild_member_uniqueconstraint UNIQUE (guildid, memberid)
);

DROP TABLE IF EXISTS guildMembers;
DROP TABLE IF EXISTS guilds;





