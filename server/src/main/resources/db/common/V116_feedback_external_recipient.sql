CREATE TABLE feedback_external_recipient
(
    id varchar PRIMARY KEY,
    email varchar,
    firstname varchar,
    lastname varchar,
    company_name varchar
);
ALTER TABLE feedback_requests ADD column external_recipient_id varchar;
