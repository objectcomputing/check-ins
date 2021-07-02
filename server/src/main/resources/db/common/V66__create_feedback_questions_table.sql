DROP TABLE IF EXISTS feedback_questions;

CREATE TABLE feedback_questions (
    id varchar PRIMARY KEY,
    question varchar,
    templateId varchar REFERENCES feedback_templates(id)
);