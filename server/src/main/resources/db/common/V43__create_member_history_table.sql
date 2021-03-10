drop table if exists member_history;

CREATE TABLE member_history(
    memberid varchar REFERENCES member_profile(uuid),
    teamId varchar REFERENCES team(uuid),
    change varchar,
    dateTime date,