DELETE FROM review_assignments o USING review_assignments n
WHERE o.id < n.id AND (o.reviewee_id = n.reviewee_id AND
                       o.reviewer_id = n.reviewer_id AND
                       o.review_period_id = n.review_period_id);

ALTER TABLE review_assignments
ADD CONSTRAINT unique_assignment UNIQUE (reviewee_id, reviewer_id, review_period_id)
