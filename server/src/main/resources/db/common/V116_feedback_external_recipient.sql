CREATE TABLE feedback_external_recipient
(
    id varchar PRIMARY KEY,
    email varchar,
    firstname varchar,
    lastname varchar,
    company_name varchar
);
ALTER TABLE feedback_requests ADD column external_recipient_id varchar;
ALTER TABLE feedback_external_recipient ADD CONSTRAINT feedback_requests_externalrecipientid_fkey FOREIGN KEY (id) REFERENCES feedback_requests(external_recipient_id) ON DELETE CASCADE;
