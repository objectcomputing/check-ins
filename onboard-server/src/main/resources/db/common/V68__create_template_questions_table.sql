DROP TABLE IF EXISTS template_questions;

CREATE TABLE template_questions (
    id varchar PRIMARY KEY,
    question varchar,
    templateId varchar REFERENCES feedback_templates(id),
    orderNum smallint
);