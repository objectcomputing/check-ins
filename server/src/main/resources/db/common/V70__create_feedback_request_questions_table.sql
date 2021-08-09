DROP TABLE if EXISTS feedback_request_questions;

CREATE TABLE feedback_request_questions(
      id varchar PRIMARY KEY,
      requestId varchar references feedback_requests(id),
      questionContent varchar,
      orderNum smallint
);