DROP TABLE if EXISTS frozen_template_questions;

CREATE TABLE frozen_template_questions(
      id varchar PRIMARY KEY,
      frozen_template_id varchar references frozen_templates(id),
      question_content varchar,
      question_number smallint
);

ALTER TABLE frozen_template_questions
ADD CONSTRAINT frozen_template_questions_uniqueconstraint UNIQUE (frozen_template_id, question_number);