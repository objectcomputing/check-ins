DROP TABLE IF EXISTS feedback_answers;

CREATE TABLE feedback_answers(
    id varchar PRIMARY KEY,
    answer varchar,
    questionId varchar REFERENCES feedback_request_questions(id),
    sentiment float
);