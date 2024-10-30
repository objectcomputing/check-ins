CREATE TABLE feedback_external_recipient
(
    id varchar PRIMARY KEY,
    email varchar,
    firstname varchar,
    lastname varchar,
    company_name varchar
);
ALTER TABLE feedback_requests ADD column external_recipient_id varchar;
ALTER TABLE feedback_requests ADD CONSTRAINT feedback_requests_externalrecipientid_fkey FOREIGN KEY (external_recipient_id) REFERENCES feedback_external_recipient(id) ON DELETE CASCADE;
ALTER TABLE feedback_templates ADD COLUMN is_for_external_recipient boolean;
