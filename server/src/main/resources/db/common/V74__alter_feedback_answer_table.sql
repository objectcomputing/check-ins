ALTER TABLE feedback_answers
DROP question_id;

ALTER TABLE feedback_answers
ADD COLUMN question_id varchar references frozen_template_questions(id);