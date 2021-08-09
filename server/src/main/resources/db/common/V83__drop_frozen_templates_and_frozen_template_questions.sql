ALTER TABLE feedback_answers
DROP COLUMN question_id;

ALTER TABLE feedback_answers
ADD COLUMN question_id varchar REFERENCES template_questions(id);


ALTER TABLE feedback_answers
ADD COLUMN request_id varchar REFERENCES feedback_requests(id);

DROP TABLE IF EXISTS frozen_template_questions;
DROP TABLE IF EXISTS frozen_templates;