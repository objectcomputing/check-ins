DROP TABLE IF EXISTS feedback_answers;

CREATE TABLE feedback_answers(
    id varchar PRIMARY KEY,
    answer varchar,
    question_id varchar REFERENCES feedback_request_questions(id),
    sentiment float
);