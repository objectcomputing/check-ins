DROP TABLE if EXISTS frozen_templates;

CREATE TABLE frozen_templates (
    id varchar PRIMARY KEY,
    creator_id varchar REFERENCES member_profile(id),
    title varchar,
    description varchar,
    request_id varchar REFERENCES feedback_requests(id)
);