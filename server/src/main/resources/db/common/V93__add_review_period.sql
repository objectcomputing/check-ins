DROP TABLE IF EXISTS review_periods;

CREATE TABLE review_periods (
  id varchar PRIMARY KEY,
  name varchar,
  open boolean
);

ALTER TABLE feedback_requests
ADD COLUMN review_period_id varchar REFERENCES review_periods(id);