DROP TABLE IF EXISTS settings;

CREATE TABLE settings (
    id varchar PRIMARY KEY,
    name varchar,
    userid varchar REFERENCES member_profile(id),
    value varchar
);