DROP TABLE IF EXISTS review_assignments;

CREATE TABLE review_assignments (
  id varchar PRIMARY KEY,
  reviewee_id varchar,
  reviewer_id varchar,
  review_period_id varchar,
  approved boolean
);
