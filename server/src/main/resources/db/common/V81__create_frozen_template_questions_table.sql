DROP TABLE if EXISTS feedback_request_questions;

CREATE TABLE frozen_template_questions(
      id varchar PRIMARY KEY,
      frozen_template_id varchar references frozen_templates(id),
      question_content varchar,
      order_num smallint
);