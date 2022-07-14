drop table if exists member_history;

CREATE TABLE member_history(
    memberId varchar REFERENCES member_profile(id),
    teamId varchar REFERENCES team(id),
    change varchar,
    dateTime date

);