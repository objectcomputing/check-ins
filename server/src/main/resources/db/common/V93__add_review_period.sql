DROP TABLE IF EXISTS review_periods;

CREATE TABLE review_periods (
  id varchar PRIMARY KEY,
  name varchar,
  open boolean,
  review_template_id varchar REFERENCES feedback_templates(id),
  self_review_template_id varchar REFERENCES feedback_templates(id)
);

ALTER TABLE feedback_requests
ADD COLUMN review_period_id varchar REFERENCES review_periods(id);