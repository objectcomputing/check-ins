CREATE TABLE refresh_token
(
    id varchar PRIMARY KEY,
    refresh_token varchar,
    user_name varchar,
    date_created timestamp,
    revoked boolean
);
