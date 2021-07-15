DROP TABLE if EXISTS frozen_templates;

CREATE TABLE frozen_templates (
    id varchar PRIMARY KEY,
    createdBy varchar REFERENCES member_profile(id),
    title varchar,
    description varchar,
    requestId varchar REFERENCES feedback_requests(id)
);