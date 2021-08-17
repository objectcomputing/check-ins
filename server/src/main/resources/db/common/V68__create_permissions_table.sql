drop table if exists permissions;

CREATE TABLE permissions(
    id varchar primary key,
    permission varchar UNIQUE
);