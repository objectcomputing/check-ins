ALTER TABLE feedback_answers
ADD CONSTRAINT feedback_answers_uniqueconstraint UNIQUE (question_id, request_id);