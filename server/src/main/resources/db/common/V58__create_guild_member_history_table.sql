drop table if exists guild_member_history;

CREATE TABLE guild_member_history(
    id varchar PRIMARY KEY,
    guildId varchar REFERENCES guild_member(guildId),
    memberId varchar REFERENCES member_profile(id),
    change varchar,
    dateTime date
);