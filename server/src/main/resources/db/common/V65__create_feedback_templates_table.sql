DROP TABLE IF EXISTS feedback_templates;

CREATE TABLE feedback_templates (
    id varchar PRIMARY KEY,
    createdBy varchar REFERENCES member_profile(id),
    title varchar,
    description varchar,
    active boolean
);
