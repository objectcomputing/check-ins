DROP TABLE IF EXISTS feedback_request;

CREATE TABLE feedback_request (
  id varchar PRIMARY KEY,
  creatorId varchar REFERENCES member_profile(id),
  requesteeId varchar REFERENCES member_profile(id),
  templateId varchar,
  sendDate date,
  dueDate date,
  status varchar
);
